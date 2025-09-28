package com.mttk.orche.agent.webSearch.query;

import com.mttk.orche.addon.AdapterConfig;

public class WebQueryFacatory {
    public static WebQuery getWebQuery(AdapterConfig a) throws Exception {
        String type = a.getString("queryApiType", "serper");
        if (type.equals("serper")) {
            return new SerperApiClient();
        }
        throw new RuntimeException("Unsupported web query type: " + type);
    }
}
