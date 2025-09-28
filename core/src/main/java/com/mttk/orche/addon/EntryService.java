package com.mttk.orche.addon;

import com.mttk.orche.core.PersistService;

/**
 * 入口服务基础类<br>
 * 不建议从此接口直接实现入口,而是继承合适的入口虚拟类
 *
 */
public interface EntryService extends PersistService, Activeable {
}
