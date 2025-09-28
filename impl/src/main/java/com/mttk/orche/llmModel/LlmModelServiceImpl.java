package com.mttk.orche.llmModel;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.service.LlmModelService;

@ServiceFlag(key = "llmModelService", name = "LLM管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class LlmModelServiceImpl extends AbstractCacheBeanService<AdapterConfig> implements LlmModelService {

}
