package com.mttk.orche.userSetting;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistWithListenerService;
import com.mttk.orche.service.UserSettingService;

@ServiceFlag(key = "userSettingService", name = "用户配置", description = "",type=SERVICE_TYPE.SYS,i18n="/com/mttk/api/impl/i18n")
public class UserSettingServiceImpl extends AbstractPersistWithListenerService  implements UserSettingService {

}
