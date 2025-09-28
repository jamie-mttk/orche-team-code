package com.mttk.orche.systemConfig;

import java.util.List;

import org.bson.Document;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.SystemConfigService;

@ServiceFlag(key = "systemConfigService", name = "系统配置服务", description = "", type = SERVICE_TYPE.USUAL, i18n = "/com/mttk/api/solution/i18n")

public class SystemConfigServiceImpl extends AbstractPersistService implements SystemConfigService {
    // 如果数据库里存在则读取,否则创建一条记录(不会调用save方法)
    @Override
    public Document obtain() throws Exception {
        List<Document> list = find(null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        //
        Document doc = new Document();
        doc.put("httpPort", 7474);
        //
        return doc;
    }

    @Override
    public String save(Document doc) throws Exception {
        //
        String id = super.save(doc);
        //
        SystemConfigUti.apply(doc);
        // 不需要再次保存,因为没有被修改
        // return super.update(doc);
        return id;
    }
}
