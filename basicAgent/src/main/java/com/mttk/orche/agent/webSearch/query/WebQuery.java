package com.mttk.orche.agent.webSearch.query;

import java.util.List;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.agent.webSearch.SearchItem;

public interface WebQuery {
    public List<SearchItem> query(String keyWord, AdapterConfig config) throws Exception;
}
