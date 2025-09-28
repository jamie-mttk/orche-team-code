package com.mttk.orche.http;

import org.eclipse.jetty.server.Server;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Panel;
import com.mttk.orche.addon.annotation.ui.Tab;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.addon.impl.AbstractListenerService;
import com.mttk.orche.http.util.JettyUtil;

@ServiceFlag(key = "httpEntryService", name = "HTTP监听", description = "处理所有的HTTP入口请求", type = SERVICE_TYPE.ENTRY_NONE, i18n = "/com/mttk/api/addon/sys/i18n")
@Control(key = "cors", label = "CORS", mode = "chooser", size = 1, props = { "type:resource.cors" })
@Control(key = "uriMatchMode", label = "URI匹配方式", mode = "select", defaultVal = "STARTWITH", size = 1, mandatory = true, props = "options:STARTWITH:开头匹配,EQUAL:相等,SPRING:Spring MVC模式")

@Control(key = "threadPoolCustomize", label = "设置线程池", mode = "checkbox", size = 1, defaultVal = "false")
@Panel(key = "threadPoolPanel", label = "", bindings = { "show:this.data.threadPoolCustomize" })
@Control(key = "threadPoolMaxThreads", parent = "threadPoolPanel", label = "最大线程数", defaultVal = "20", size = 1, props = "dataType:number")
@Control(key = "threadPoolMinThreads", parent = "threadPoolPanel", label = "最小线程数", defaultVal = "4", size = 1, props = "dataType:number")
@Control(key = "threadPoolIdleTimeout", parent = "threadPoolPanel", label = "空闲超时(毫秒)", defaultVal = "60000", size = 1, props = "dataType:number")
// Added by Jamie @2022/12/28
@Table(key = "attributes", label = "系统属性", props = { "width:30%", "labelWidth: 120px" })
@Control(key = "name", label = "属性名", size = 1, mandatory = true, parent = "attributes")
@Control(key = "value", label = "属性值", size = 1, mandatory = true, parent = "attributes")

/* 连接相关的tab */
@Tab(key = "connectorTab", label = "连接", parent = "_parent")
@Table(key = "connectors", label = "连接", props = { "cols:protocol,host,port,active",
		"showTitle:false" }, parent = "connectorTab")
@Control(key = "active", parent = "connectors", label = "激活", mode = "checkbox", defaultVal = "true")
@Control(key = "protocol", label = "协议", mode = "select", defaultVal = "HTTP", parent = "connectors", mandatory = true, props = "options:HTTP,HTTPS")
@Control(key = "host", parent = "connectors", label = "绑定IP")
@Control(key = "port", parent = "connectors", label = "监听端口", mandatory = true, props = "dataType:number")
@Control(key = "idleTimeout", parent = "connectors", label = "空闲超时(秒)", props = "dataType:number")

@Panel(key = "httpsPanel", parent = "connectors", label = "", bindings = { "show:this.data.protocol=='HTTPS'" })
@Control(key = "cert", parent = "httpsPanel", label = "私钥和证书", mode = "popup", props = {
		"type:cert" }, mandatory = true)
@Control(key = "sessionCacheSize", parent = "httpsPanel", label = "会话缓存大小", mandatory = true, defaultVal = "100", props = "dataType:number")
@Control(key = "excludeUnsafeCipherSuites", mode = "checkbox", defaultVal = "true", parent = "httpsPanel", label = "排除不安全的加密套件")
@Control(key = "excludeCipherSuites", parent = "httpsPanel", label = "自定义排除的加密套件", bindings = {
		"show:!this.data.excludeUnsafeCipherSuites" })
@Control(key = "includeCipherSuites", parent = "httpsPanel", size = 1, label = "包含的加密套件")
@Control(key = "excludeUnsafeProtocols", mode = "checkbox", defaultVal = "true", parent = "httpsPanel", label = "排除不安全的协议")
@Control(key = "excludeProtocols", parent = "httpsPanel", label = "自定义排除的协议", bindings = {
		"show:!this.data.excludeUnsafeProtocols" })
@Control(key = "includeProtocols", parent = "httpsPanel", size = 1, label = "包含的协议")

@Control(key = "clientAuth", parent = "httpsPanel", label = "客户端验证", mode = "select", defaultVal = "NO", props = "options:NO:否,YES:需要")
@Table(key = "clientAuthCertsTable", parent = "httpsPanel", label = "允许的客户端证书", bindings = "show:this.data.clientAuth=='YES'", props = {
		"operates:_add,_edit,_delete", "cols:cert", "width:35%", "labelWidth: 120px" })
@Control(key = "cert", parent = "clientAuthCertsTable", mandatory = true, label = "证书", size = 1, mode = "popup", props = {
		"type:cert" })

/* 端口相关的tab */
@Tab(key = "pathTab", label = "路径映射", parent = "_parent")
@Table(key = "paths", label = "路径映射", props = { "operates:_add,_edit,_delete,_copy,_up,_down",
		"cols:uri,method,active,type", "showTitle:false" }, parent = "pathTab")
@Control(key = "uri", parent = "paths", mandatory = true, label = "路径")
@Control(key = "method", label = "方法", mode = "select", defaultVal = "*", parent = "paths", mandatory = true, props = "options:*:All,GET,POST,PUT,DELETE,OPTIONS,HEAD,CONNECT,TRACE,PATCH,MOVE,COPY,LINK,UNLINK,WRAPPED")
@Control(key = "type", label = "类型", mode = "select", defaultVal = "process", parent = "paths", mandatory = true, props = "options:process:流程,war:WAR包,service:服务,redirect:重定向,directory:目录,file:文件")
@Control(key = "active", parent = "paths", label = "激活", mode = "checkbox", defaultVal = "true")
// process
@Control(key = "process", label = "流程", mode = "chooser", size = 1, mandatory = true, parent = "paths", props = {
		"type:process" }, bindings = { "show:this.data.type=='process'" })
@Control(key = "runtimeStrategy", label = "执行策略", mode = "chooser", size = 1, parent = "paths", props = {
		"type:runtimeStrategy" }, bindings = { "show:this.data.type=='process'||this.data.type=='service'" })

@Control(key = "contentToPayload", label = "保存HTTP请求体", mode = "checkbox", parent = "paths", defaultVal = "false", bindings = {
		"show:this.data.type=='process'" })

@Control(key = "processErrorHandler", label = "错误处理方式", defaultVal = "ERROR500", mode = "select", size = 1, parent = "paths", props = {
		"options:ERROR500:500报错,IGNORE:忽略错误流程给出反馈,CUSTOMIZE:自定义,PROCESS:流程" }, bindings = {
				"show:this.data.type=='process'" })
// processErrorHandler为 自定义 时
@Control(key = "pehCustomizeCode", label = "出错HTTP代码", defaultVal = "500", size = 1, parent = "paths", props = "dataType:number", bindings = {
		"show:this.data.type=='process' && this.data.processErrorHandler=='CUSTOMIZE'" })
@Control(key = "pehCustomizeContentType", label = "出错Content type", defaultVal = "application/json", size = 1, parent = "paths", bindings = {
		"show:this.data.type=='process' && this.data.processErrorHandler=='CUSTOMIZE'" })
// _ie_开头说明需要自己eval
@Control(key = "_ie_pehCustomizeContent", label = "出错HTTP内容", description = "此处会在流程执行完成后计算表达式", defaultVal = "", size = 1, parent = "paths", mode = "editor", props = {
		"language:json",
		"height:160" }, bindings = { "show:this.data.type=='process' && this.data.processErrorHandler=='CUSTOMIZE'" })
// processErrorHandler为 流程 时
@Control(key = "pehProcess", label = "出错处理流程", size = 1, parent = "paths", mode = "chooser", mandatory = true, props = {
		"type:process" }, bindings = { "show:this.data.type=='process' && this.data.processErrorHandler=='PROCESS'" })
@Control(key = "pehRuntimeStrategy", label = "出错执行策略", size = 1, parent = "paths", mode = "chooser", props = {
		"type:runtimeStrategy" }, bindings = {
				"show:this.data.type=='process' && this.data.processErrorHandler=='PROCESS'" })

// para
@Table(key = "para", label = "参数", parent = "paths", bindings = {
		"show:this.data.type=='service'" }, props = { "width:30%", "labelWidth: 120px" })
@Control(key = "key", parent = "para", mandatory = true, label = "键", size = 1)
@Control(key = "value", parent = "para", mandatory = true, label = "值", size = 1)
// meta
@Table(key = "meta", label = "流程元数据", parent = "paths", bindings = {
		"show:this.data.type=='process'" }, props = { "width:30%", "labelWidth: 120px" })
@Control(key = "key", parent = "meta", mandatory = true, label = "键", size = 1)
@Control(key = "value", parent = "meta", mandatory = true, label = "值", size = 1)
// strategy for process

// @Panel(key="processStrategyPanel",parent="paths",bindings={"show:this.data.type=='process'"})
// @Control(key = "async",parent="processStrategyPanel", label = "异步调度", mode =
// "checkbox", defaultVal = "false")
// @Control(key = "channel",label="执行通道", mode =
// "select",parent="processStrategyPanel",props= {"url:dispatch/query"})
// @Control(key = "strategyRetry",label="重试策略", mode =
// "select",parent="processStrategyPanel",props= {"options:RETRY:重试,IGNORE:忽略"})
// @Control(key = "strategyPersistent",label="持久化策略", mode =
// "select",parent="processStrategyPanel",props=
// {"options:END:结束记录,START_END:开始和结束记录,NONE:不记录,ALL:每步都记录"})
// @Control(key = "strategyLog",label="日志策略", mode =
// "select",parent="processStrategyPanel",props=
// {"options:ALL:全部记录,NONE:不记录,SMART:记录重要"})
// @Control(key = "strategyCheckpointStart",label="开始检查点策略", mode =
// "select",parent="processStrategyPanel",props=
// {"options:SAVE_DELETE:开始记录成功删除,NO_SAVE:不保存,ALWAYS_SAVE:一直保存"})
// @Control(key = "strategyCheckpoint",label="执行检查点策略", mode =
// "select",parent="processStrategyPanel",props=
// {"options:SAVE_ERROR:出错时记录,NO_SAVE:不保存,ALWAYS_SAVE:一直保存"})
// @Control(key = "strategyExceptionDump",label="异常DUMP策略", mode =
// "select",parent="processStrategyPanel",props= {"options:SIMPLE:简单,FULL:完整"})
// @Control(key = "debugMode",parent="processStrategyPanel", label = "调试模式",
// mode = "checkbox", defaultVal = "false")

// war包
@Control(key = "targetWar", label = "WAR包", parent = "paths", size = 1, bindings = { "show:this.data.type=='war'" })
@Control(key = "welcome", label = "index文件", parent = "paths", size = 1, bindings = { "show:this.data.type=='war'" })
// 服务
// changed by Jamie @1020/2/17
// 从input修改为select
// @Control(key = "targetService", label = "服务名", parent = "paths", size = 1,
// mandatory = true, bindings = {
// "show:this.data.type=='service'" })
@Control(key = "targetService", mode = "select", label = "服务", parent = "paths", size = 1, mandatory = true, props = {
		"url:server/query", "valueKey:key", "labelKey:name", "filterable:true" }, bindings = {
				"show:this.data.type=='service'" })
// 重定向
@Control(key = "targetRedirectUrl", label = "重定向URL", parent = "paths", size = 1, bindings = {
		"show:this.data.type=='redirect'" })
// 目录
@Control(key = "targetDirectory", label = "根目录", parent = "paths", size = 1, bindings = {
		"show:this.data.type=='directory'" })
@Control(key = "indexFile", label = "index文件", parent = "paths", size = 1, bindings = {
		"show:this.data.type=='directory'" })
@Control(key = "dirListing", parent = "paths", label = "允许目录列表", mode = "checkbox", defaultVal = "false", bindings = {
		"show:this.data.type=='directory'" })
// 文件
@Control(key = "targetFile", label = "文件", parent = "paths", size = 1, bindings = { "show:this.data.type=='file'" })

// 公共的
@Control(key = "showPrePost", label = "预/后处理", mode = "checkbox", defaultVal = "false", size = 1, parent = "paths", bindings = {
		"show:this.data.type=='process'||this.data.type=='war'||this.data.type=='service'" })
@Panel(key = "prePostPanel", label = "", parent = "paths", bindings = {
		"show:this.data.showPrePost && (this.data.type=='process'||this.data.type=='war'||this.data.type=='service')" })
@Control(key = "preprocess", label = "预处理流程", mode = "chooser", size = 1, parent = "prePostPanel", props = {
		"type:process" })
@Control(key = "preprocessExit", parent = "prePostPanel", label = "退出条件", description = "当条件满足时不处理实际的流程/WAR/服务/后处理", mode = "select", defaultVal = "no", mandatory = true, props = "options:no:不退出,retTrue:返回true时,retFalse:返回false时,httpCodeError:HTTP错误代码,httpCodeSuccess:HTTP正确代码")
@Control(key = "preprocessStrategy", label = "预处理执行策略", mode = "chooser", parent = "prePostPanel", props = {
		"type:runtimeStrategy" })
@Control(key = "postprocess", label = "后处理流程", mode = "chooser", size = 1, parent = "prePostPanel", props = {
		"type:process" })
@Control(key = "postprocessExecute", parent = "prePostPanel", label = "执行条件", description = "当条件满足时才执行后处理流程", mode = "select", defaultVal = "yes", mandatory = true, props = "options:yes:一直执行,retTrue:返回true时,retFalse:返回false时,httpCodeError:HTTP错误代码,httpCodeSuccess:HTTP正确代码")
@Control(key = "postprocessStrategy", label = "后处理执行策略", mode = "chooser", parent = "prePostPanel", props = {
		"type:runtimeStrategy" })

public class HttpEntryService extends AbstractListenerService<JettyServerWrap> {
	// private Logger logger=LoggerFactory.getLogger(HttpEntryService.class);

	// 创建JettyServer
	@Override
	protected JettyServerWrap createObject(AdapterConfig config) throws Exception {
		//
		
		// 这里也可以把doc换成RuntimeConfig
		// AdapterConfig config=context.createAdapterConfig(context.eval(doc, null));
		//
		Server jettyServer = JettyUtil.createHttpEntity(this.context, config);

		return new JettyServerWrap(jettyServer, config);
	}
}
