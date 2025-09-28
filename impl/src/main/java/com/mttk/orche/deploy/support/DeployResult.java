package com.mttk.orche.deploy.support;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.util.ThrowableUtil;

public class DeployResult {

	// 找到的services
	List<Document> services = new ArrayList<>();
	// 找到的agentTemplates
	List<Document> agentTemplates = new ArrayList<>();
	// 找到的plugins
	List<Document> plugins = new ArrayList<>();

	// 遇到的异常
	List<Throwable> throwables = new ArrayList<>();

	public List<Document> getServices() {
		return services;
	}

	public List<Document> getAgentTemplates() {
		return agentTemplates;
	}

	public List<Document> getPlugins() {
		return plugins;
	}

	public List<Throwable> getThrowables() {
		return throwables;
	}

	public void addService(Document service) {
		getServices().add(service);
	}

	public void addAgentTemplate(Document agentTemplate) {
		getAgentTemplates().add(agentTemplate);
	}

	public void addPlugin(Document plugin) {
		getPlugins().add(plugin);
	}

	public void addThrowable(Throwable throwable) {
		getThrowables().add(throwable);
	}

	public void merge(DeployResult result) {
		if (result == null) {
			return;
		}

		result.getServices().forEach((e) -> {
			addService(e);
		});
		result.getAgentTemplates().forEach((e) -> {
			addAgentTemplate(e);
		});

		result.getPlugins().forEach((e) -> {
			addPlugin(e);
		});

		result.getThrowables().forEach((e) -> {
			addThrowable(e);
		});
	}

	public Document toDocument() {
		Document doc = new Document();
		//
		doc.append("services", getServices()).append("agentTemplates", getAgentTemplates())
				.append("plugins", getPlugins()).append("throwables",
						dumpThrowables());
		//
		return doc;
	}

	private List<String> dumpThrowables() {
		List<Throwable> list = getThrowables();
		List<String> l = new ArrayList<>(list.size());
		for (Throwable t : list) {
			// Document doc=new Document();
			// doc.append(t.getClass().toString(), );
			// l.add(doc);
			l.add(ThrowableUtil.dumpSimple(t));
		}
		return l;
	}

}
