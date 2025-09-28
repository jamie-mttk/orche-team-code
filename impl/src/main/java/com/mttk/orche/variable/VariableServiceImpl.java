package com.mttk.orche.variable;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.service.ScriptService;
import com.mttk.orche.service.VariableService;

@ServiceFlag(key = "variableService", name = "变量管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class VariableServiceImpl extends AbstractCacheBeanService<AdapterConfig> implements VariableService {
	@Override
	public void doStart() throws Exception {
		getStrategy().setIdentifyField("key");
		//
		super.doStart();
	}

	@Override
	public Object eval(String key) throws Exception {
		AdapterConfig config = obtainBean(key);
		if (config == null) {
			throw new RuntimeException("No variable is found by key:" + key);
		}
		//
		String expression = config.getString("expression");
		//
		ScriptService scriptService = server.getService(ScriptService.class);
		return scriptService.eval(scriptService.createEnv(), expression);
	}

}
