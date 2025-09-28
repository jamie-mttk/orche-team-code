package com.mttk.orche.service;

import org.bson.Document;

import com.mttk.orche.core.PersistService;

public interface SystemConfigService extends PersistService {
    public Document obtain() throws Exception;
}
