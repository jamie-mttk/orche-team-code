package com.mttk.orche.deploy;

import java.io.File;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Filters;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.deploy.support.DeployResult;

import com.mttk.orche.deploy.support.PackageParser;
import com.mttk.orche.service.AgentTemplateService;
import com.mttk.orche.service.DeployService;
import com.mttk.orche.service.DeployService.TYPE;
import com.mttk.orche.service.support.DeployStrategy;
import com.mttk.orche.support.MongoUtil;

@ServiceFlag(key = "deployService", name = "部署管理", depends = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class DeployServiceImpl extends AbstractPersistService implements DeployService {
	private static Logger logger = LoggerFactory.getLogger(DeployServiceImpl.class);

	@Override
	public Document deploy(File file, String name, String pattern, DeployStrategy strategy) throws Exception {
		DeployResult result = null;
		try {
			result = PackageParser.parse(server, file, name, pattern);

		} catch (Exception e) {
			throw new Exception("Fail to parse " + file, e);
		}

		//
		if (strategy.isParseOnly()) {
			return result.toDocument();
		}
		if (result.getThrowables().size() > 0 && !strategy.isForceSave()) {
			return result.toDocument();
		}

		// 由于保存后会产生新的Document,因此产生一个新的DeplyReult
		DeployResult r = new DeployResult();
		// 拷贝throwables
		result.getThrowables().forEach(t -> r.addThrowable(t));
		// 处理folder

		// 处理Service
		for (Document f : result.getServices()) {

			//
			appendPackName(f, name);
			// 判断是否被禁止部署了
			if (isDisabed(TYPE.SERVICE, f.getString("key"))) {
				f.append("status", "suppress");
			} else {
				// 判断是否存在
				Bson filter = Filters.and(Filters.eq("category", f.getString("category")),
						Filters.eq("key", f.getString("key")));
				Optional<Document> doc = server.load(filter);
				if (doc.isPresent()) {

					//
					if (strategy.isOverwrite()) {
						f.put("_id", doc.get().getObjectId("_id"));
						// 如果doc里有props则拷贝出来保留
						if (doc.get().get("props") != null) {
							f.put("props", doc.get().get("props"));
						}
						//
						server.replace(f);
						//
						f.append("status", "replace");
					} else {
						f.append("status", "ignore");
					}
				} else {
					server.insert(f);
					//
					f.append("status", "new");
				}
			}
			//
			addOrUpdate(TYPE.SERVICE, f);
			r.addService(f);
		}
		//

		// 处理agentTemplate
		final AgentTemplateService agentTemplateService = server.getService(AgentTemplateService.class);
		for (Document f : result.getAgentTemplates()) {
			//
			appendPackName(f, name);
			// 判断是否被禁止部署了
			if (isDisabed(TYPE.AGENT_TEMPALTE, f.getString("key"))) {
				f.append("status", "suppress");
			} else {
				// 判断是否存在
				Bson filter = Filters.and(Filters.eq("type", "agentTemplate"), Filters.eq("key", f.getString("key")));
				Optional<Document> doc = agentTemplateService.load(filter);
				if (doc.isPresent()) {
					// logger.info("AgentTemplate already exists: " + f.getString("key") + " with
					// overwrite:"
					// + strategy.isOverwrite());
					if (strategy.isOverwrite()) {
						//
						f.put("_id", doc.get().getObjectId("_id"));
						agentTemplateService.replace(f);
						//
						f.append("status", "replace");
					} else {
						f.append("status", "ignore");
					}
				} else {
					agentTemplateService.insert(f);
					//
					f.append("status", "new");
				}
			}
			//
			addOrUpdate(TYPE.AGENT_TEMPALTE, f);
			r.addAgentTemplate(f);
		}

		//
		// System.out.println(r);
		//
		return r.toDocument();
	}

	@Override
	public boolean delete(String id) throws Exception {
		// 必须先load,否则delete完了后就没有了
		Optional<Document> o = load(id);
		//
		super.delete(id);
		// 先从原来的服务器删除

		if (o.isPresent()) {
			return removeFromRaw(o.get());
		} else {
			return false;
		}
	}

	@Override
	public boolean suppress(String id) throws Exception {
		// 先从原来的服务器删除
		Optional<Document> o = load(id);
		if (o.isPresent()) {
			Document doc = o.get();
			removeFromRaw(doc);
			//
			doc.append("suppress", true);
			//
			update(doc);
			//
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean unsuppress(String id) throws Exception {
		// 先从原来的服务器删除
		Optional<Document> o = load(id);
		if (o.isPresent()) {
			Document doc = o.get();
			//
			doc.append("suppress", false);
			update(doc);
			//
			return true;
		} else {
			return false;
		}
	}

	// 根据TYPE不同分别执行删除
	private boolean removeFromRaw(Document doc) throws Exception {
		TYPE type = TYPE.valueOf(doc.getString("type"));
		String pk = doc.getString("pk");
		switch (type) {
			case SERVICE:
				return server.delete(pk);
			case AGENT_TEMPALTE:
				return server.getService(AgentTemplateService.class).delete(pk);
		}
		//
		return false;
	}

	// 是否被禁用
	private boolean isDisabed(TYPE type, String key) throws Exception {
		Optional<Document> o = findByTypeAndKey(type, key);
		if (!o.isPresent()) {
			// 新组件肯定没有被禁用
			return false;
		}
		//
		// System.out.println(isDisabed(o.get())+"\t"+o.get());
		return isDisabed(o.get());
	}

	// 检查给定id是否被禁止部署
	private boolean isDisabed(Document doc) throws Exception {
		//
		return doc.getBoolean("suppress", false);
	}

	// 根据类型和组件id查找
	private Optional<Document> findByTypeAndKey(TYPE type, String key) throws Exception {
		Bson filter = Filters.and(Filters.eq("type", type.name()), Filters.eq("key", key));
		return load(filter);
	}

	// 插入或修改deploy表
	private void addOrUpdate(TYPE type, Document doc) throws Exception {
		Optional<Document> o = findByTypeAndKey(type, doc.getString("key"));
		if (o.isPresent()) {
			// 存在,修改name,description,pk
			Document d = o.get();
			copy(d, doc);
			//
			replace(d);
		} else {
			// 新
			Document d = new Document();
			copy(d, doc);
			d.append("type", type.name()).append("key", doc.getString("key")).append("suppress", false);
			//
			insert(d);
		}
	}

	// 把doc的有价值的值拷贝到d
	private void copy(Document d, Document doc) {
		d.append("name", doc.getString("name")).append("description", doc.getString("description"))
				.append("pk", MongoUtil.getId(doc)).append("serviceClass", doc.getString("serviceClass"));
	}

	// 在组件描述加入包名称
	private void appendPackName(Document d, String name) {
		d.append("_package_name", name);
	}
}
