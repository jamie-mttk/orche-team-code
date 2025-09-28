package com.mttk.orche.systemConfig;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mttk.orche.http.HttpEntryService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.IOUtil;

public class SystemConfigUti {
    public static void apply(Document doc) throws Exception {
        applyWeb(doc);
    }

    private static void applyWeb(Document doc) throws Exception {

        HttpEntryService httpEntryService = ServerUtil.getService(HttpEntryService.class);
        //
        Optional<Document> o = httpEntryService.load(Filters.eq("webSystem", true));
        Document d = null;
        if (o.isPresent()) {
            d = o.get();

        } else {
            // 否则,不存在,需要创建
            try (InputStream is = SystemConfigUti.class.getResourceAsStream("webEntry.json")) {
                String json = IOUtil.convertToString(is);
                d = Document.parse(json);
            }
        }
        //
        List<Document> connectors = d.getList("connectors", Document.class);
        if (connectors != null && connectors.size() > 0) {
            int httpPort = doc.getInteger("httpPort", 7474);
            connectors.get(0).append("port", httpPort);
        }
        //
        httpEntryService.save(d);
        //
        return;
    }
}
