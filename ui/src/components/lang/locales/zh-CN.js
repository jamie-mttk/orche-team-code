export default {
  _: {
    key: '键',
    id: '编号',
    value: '值',
    name: '名称',
    description: '描述',
    type: '类型',
    operation: '操作',
    ok: '确定',
    cancel: '取消',
    close: '关闭',
    add: '增加',
    del: '删除',
    edit: '修改',
    save: '保存',
    copy: '拷贝',
    up: '上移',
    down: '下移',
    view: '查看',
    import: '导入',
    export: '导出',
    upload: '上传',
    download: '下载',
    exportExcel: '下载表格',
    search: '查询',
    choose: '选择',
    reset: '重置',
    clear: '清除',
    refresh: '刷新',
    start: '启动',
    stop: '停止',
    success: '成功',
    fail: '失败',
    parent: '上级',
    all: '全部',
    active: '激活',
    deactive: '禁用',
    batchActive: '批量激活',
    batchDeactive: '批量禁用',
    tableNoData: '暂无数据,请单击增加',
    tableNoData2: '无数据',
    confirm: '确认',
    confirmDel: '是否确认删除?',
    delSuccess: '删除成功',
    delFail: '删除失败',
    confirmSave: '是否确认保存?',
    saveSuccess: '保存成功',
    saveFail: '保存失败',
    upload_exceed: '每次只允许上传一个文件',
    upload_no_file: '请选择希望上传的文件',
    uploadSuccess: '上传成功',
    uploadFail: '上传失败',
    downloadFail: '下载失败',
    copySuccess: '拷贝成功',
    encoding: '编码',
    more: '更多',
    insertTime: '创建时间',
    updateTime: '更新时间',
    expertMode: '专家模式',
    yes: '是',
    no: '否',
    pleaseChoose: '请选择',
    choosed: '已选择',
    unchoosed: '未选择',
    lang: {
      notify: '语言切换到 {0} 成功,你可能需要刷新界面后让修改起效!'
    }
  },
  __: {
    entry: '入口',
    process: '流程',
    reentry: '重入点',
    pool: '连接池',
    resource: '资源',
    mapper: '网页映射',
    scheduler: '定时器',
    retryStrategy: '重试策略',
    variable: '变量',
    sequence: '自增序号',
    runtimeStrategy: '执行策略',
    channel: '通道',
    cache: '缓存',
    lookup: '查找表',
    authentication: '认证库',
    authorization: '授权库',
    solutionDm: 'API解决方案',
    solutionRms: '收转发解决方案',
    status: '状态',
    docType: '报文类型',
    domain: '域',
    subject: '标题',
    sender: '发送方',
    receiver: '接收方',
    transaction: '报文编号',
    payloadViewer: '报文查看',
    plsChooseRows: '请首先选择希望操作的数据行',
    sureToOperate: '确定继续操作吗',
    doneWithResult: '操作成功条数:',
    dataSize: '数据大小',
    dataCount: '条数'
  },
  layout: {
    setting: {
      title: '系统界面设置',
      themeChoose: '样式选择',
      themeSample: '显示样例'
    }
  },
  _authorization: {
    AuthenticationChooser: {
      principal: '认证主体(用户名)'
    }
  },
  _Cert: {
    certType: '证书分类',
    CertAlias: {
      certList: '别名列表',
      alias: '别名',
      keyStore: '密钥库',
      certificate: '证书',
      privateKey: '私钥',
      createCert: '创建证书',
      expireDays: '过期天数',
      dn_CN: '通用名',
      dn_OU: '组织单位',
      dn_O: '组织',
      dn_L: '位置',
      dn_S: '省',
      dn_C: '国家',
      signAlgorithm: '签名算法',
      keySize: '密钥长度',
      viewCert: '查看证书'
    },
    CertKeyStore: {
      keyStoreList: '密钥库列表',
      aliasCount: '别名数量',
      keyStoreEdit: '密钥库编辑'
    },
    CertPicker: {
      chooseCert: '选择证书'
    },
    ExportCertDialog: {
      title: '导出证书',
      format: '格式',
      format_binary: '二进制(DER)'
    },
    ExportKeyStoreDialog: {
      title: '导出密钥库',
      format: '格式',
      password: '密码',
      password2: '确认密码',
      error_no_password: '没有输入密码',
      error_password_unmatch: '密码和确认密码不一致'
    },
    ExportPrivateKeyDialog: {
      title: '导出私钥',
      format: '格式',
      format_binary: '二进制(DER)',
      password: '密码',
      password2: '确认密码',
      error_no_password: '没有输入密码',
      error_password_unmatch: '密码和确认密码不一致'
    },
    ImportCertDialog: {
      title: '导入证书',
      alias: '别名',
      format: '格式',
      format_binary: '二进制(DER)',
      file: '文件',
      file_choose: '选取文件',
      file_choose_tip: '选择要上传的文件后点击右下角的确定按钮',
      error_no_alias: '别名没有输入',
      error_no_cert: '证书没有输入',
      error_only_one_file: '每次只允许上传一个文件'
    },
    ImportKeyStoreDialog: {
      title: '导入密钥库',
      format: '格式',
      password: '密码',
      file: '文件',
      file_choose: '选取文件',
      file_choose_tip: '选择要上传的文件后点击右下角的确定按钮',
      error_no_password: '密码没有输入',
      error_no_privateKey: '私钥没有输入',
      error_only_one_file: '每次只允许上传一个文件'
    },
    ImportPrivateKeyDialog: {
      title: '导入私钥',
      alias: '别名',
      format: '格式',
      format_binary: '二进制(DER)',
      privateKey_file: '私钥文件',
      privateKey_file_choose: '选取私钥文件',
      password: '密码',
      certificate_file: '证书文件',
      certificate_file_choose: '选取证书文件',
      error_no_alias: '别名没有输入',
      error_no_privateKey: '私钥没有输入',
      error_no_certificate: '证书没有输入',
      error_only_one_file: '每次只允许上传一个文件'
    }
  },
  _ConfigDyna: {
    Chooser: {
      unsuportedType: '不支持的选择类型',
      error_get_list: '获取列表错误',
      choose_to_edit: '请从列表里选择要编辑的数据',
      choose_to_del: '请从列表里选择要删除的数据'
    },
    dataChooser: {
      retryConfig: {
        name: '重试策略',
        max: '最大次数',
        interval: '间隔(秒)',
        intervalInc: '间隔增加(秒)'
      },
      schedulerConfig: {
        name: '定时器',
        startTime: '开始时间',
        endTime: '结束时间',
        mode: '模式',
        mode_options:
          'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期,COMBINATION:组合定时器',
        repeatInterval: '间隔',
        repeatUnit: '单位',
        repeatUnit_options: 'hour:小时,minute:分,second:秒,milliSecond:毫秒',
        repeatCount: '重复次数',
        repeatCount_description: '-1代表无限重复;\r\n当设置为其他值时,实际执行次数为设置值+1',
        misFire: '错过触发策略',
        misFire_description:
          '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
        misFire_simple_smart:
          'MISFIRE_INSTRUCTION_SMART_POLICY. 重复次数为0,MISFIRE_INSTRUCTION_FIRE_NOW; 重复次数为-1,MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT; 其他,MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT',
        misFire_simple_ignore: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用',
        misFire_simple_1:
          'MISFIRE_INSTRUCTION_FIRE_NOW. 如果重复次数!=0,等同于 MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;否则,立刻执行一次',
        misFire_simple_2:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT. 立刻触发一次,总触发次数不变.',
        misFire_simple_3:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT. 立刻触发一次,总触发次数扣除错过的次数.',
        misFire_simple_5:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT. 下一个触发点触发,总触发次数不变.',
        misFire_simple_4:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT. 下一个触发点触发,总触发次数扣除错过的次数.',
        misFire_other_smart:
          'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW',
        misFire_other_ignore: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用',
        misFire_other_1: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发',
        misFire_other_2: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发',
        cronExpression: 'CRON表达式',
        dailyRepeatInterval: '间隔',
        dailyRepeatUnit: '单位',
        dailyRepeatUnit_options: 'hour:小时,minute:分,second:秒',
        dailyStartTime: '每日开始',
        dailyEndTime: '每日结束',
        dailyDaysOfWeek: '执行日期',
        dailyDaysOfWeek_options: '1:周日,2:周一,3:周二,4:周三,5:周四,6:周五,7:周六',
        calendarRepeatInterval: '间隔',
        calendarRepeatUnit: '单位',
        calendarRepeatUnit_options: 'year:年,month:月,week:周,day:日,hour:小时,minute:分,second:秒',
        combinationTrigger_mode: '模式',
        combinationTrigger_mode_options:
          'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期'
      }
    },
    DateTimePicker: {
      startTime: '开始时间',
      endTime: '结束时间',
      to: '至',
      startDate: '开始日期',
      endDate: '结束日期'
    },
    support: {
      fieldMandatory: '字段不允许为空'
    },
    Select: {
      choosed: '已选择',
      unchoosed: '未选择',
      error_get_list: '获取列表失败'
    },
    Table: {
      inputConfirm: '请输入拷贝后的名称',
      copied: '的拷贝',
      copiedThis: '当前行的拷贝',
      mandatoryInput: '必须输入'
    }
  },
  _Page: {
    PageStandard: {
      active_no_url: '没有设置activeURL无法激活',
      active_confirm: '是否确认激活?',
      active_success: '激活成功',
      active_fail: '激活失败',
      deactive_no_url: '没有设置deactiveURL无法禁用',
      deactive_confirm: '是否确认禁用?',
      deactive_success: '禁用成功',
      deactive_fail: '禁用失败',
      error_get_data: '数据获取失败',
      error_get_config: '获取配置失败'
    },
    UiDialog: {
      basic: '基本信息',
      para: '配置参数',
      uiNone_label: '特别说明',
      uiNone_value: '此模块不需要任何配置',
      copyDone: '数据ID拷贝成功'
    },
    WizardDialog: {
      preStep: '上一步',
      nextStep: '下一步',
      finish: '完成',
      close_confirm: '确认关闭此向导？所有未保存数据都不会被保存.'
    }
  },
  _utils: {
    datetimePicker: {
      today: '今天',
      yesterday: '昨天',
      weekAgo: '一周前',
      firstThisMonth: '本月初',
      firstLastMonth: '上月初'
    },
    request: {
      logout_confirm: '你已被登出，可以取消继续留在当前页面，或者重新登录',
      login_again: '重新登录',
      unauth_text: '对不起,你访问的下面资源没有被授权,请与系统管理员联系',
      unauth: '访问未授权',
      error500: {
        title: '500错误',
        cause: '类型',
        error: '错误',
        detail: '详情'
      }
    }
  },
  _Plugin: {
    define: '定义',
    data: '数据',
    noData: '没有数据',
    error_no_edit: '此插件不允许编辑数据'
  },
  _solution: {
    Related: {
      cache: '缓存',
      authentication: '认证库',
      authorization: '授权库',
      pool: '池',
      entry: '入口',
      resource: '资源',
      process: '流程',
      related: '相关对象'
    }
  },
  _scriptBuilder: {
    data: {
      tips:
        '脚本由系统在调用前自动计算,用于提供动态的调用参数.<br>' +
        '系统支持如下类型类型定义.注意不同类型脚本不能混合使用.<br>' +
        '编辑时不需要添加前缀.<br>' +
        '<b>非脚本</b>  不以后续前缀开头的文本被认为不是脚本,不会被计算.<br>' +
        '<b>管道值</b>  以p:开头,用于获取指定键的管道值.<br> HTTP调用时http_para_xyz可以获取HTTP参数xyz<br>' +
        '<b>变量</b>  以v:开头,获取指定变量的值.<br>' +
        '<b>mvel脚本</b>  以m:开头,Mvel2.x脚本,适合于复杂的计算场景.<br>' +
        '<b>Jodd模板</b>  以j:开头,适合于大量文本内添加少量变量,如邮件模板.' +
        '<b>管道p</b>  包括如下常用键：_subject(标题),_sender(发送方),_receiver(接收方),_transaction(事务编号,如AS2消息编号),_doc_type(报文类型),' +
        '_result(流程上一个节点执行的返回值),_payload(缺省报文),_source_type(流程来源,包括ENTRY、ASYNC、HTTP_PROCESS/HTTP_PRE/HTTP_POST),' +
        '_source_id(来源编号,根据不同来源含义不同),_source_name(来源名称,根据不同来源含义不同),_time_entry(入口接收到数据的时间,格式为 yyyy-MM-dd HH:mm:ss)<br>' +
        '另外在API调用流程中可以通过http_para_xyz获取HTTP参数xyz的值,http_header_efg获取HTTP头efg的值<br>' +
        '<b>事件(event)</b>  包括如常用键:type(事件类型,service:服务,process:流程,sys:系统),source(来源),sourceSub(子来源),content(错误内容),exception(异常对象);' +
        'type=service时还有:infos(infoMap),payloads(报文列表);type=process时还有data(日志数据),pipeline(管道数据),node(当前节点)',
      utilMethods: {
        now1: '得到当前日期,格式yyyyMMddHHmmss',
        now2: '得到当前日期,格式由参数给出',
        now2_pattern: '格式',
        yesterday1: '得到昨天日期,格式yyyyMMddHHmmss',
        yesterday2: '得到昨天日期,格式由参数给出',
        yesterday2_pattern: '格式',
        uuid: '得到UUID,32位,可以做唯一标识',
        sequence: '得到自增变量的值',
        sequence_key: '键',
        sequence_delta: '自增量',
        notEmpty: '值不为空',
        notEmpty_val: '值',
        isEmpty: '值为空',
        isEmpty_val: '值',
        serverSetting: '得到服务器设置值',
        serverSetting_key: '设置键',
        serverSettings: '得到服务器设置Map<String,Object>',
        sysProperty: '得到环境变量值',
        sysProperty_key: '环境变量键',
        evalVariable: '得到变量值',
        evalVariable_key: '变量',
        loadPayload: '读取指定报文(可以通过p.get("_payload")的方式获取)的值并根据编码转换成字符串',
        evalVariable_payload: '报文编号',
        evalVariable_encoding: '编码'
      },
      pipelineMethods: {
        get: '通过键得到管道值',
        get_key: '键',
        getString1: '通过键得到字符型管道值,不存在返回null',
        getString1_key: '键',
        getString2: '通过键得到字符型管道值,不存在返回缺省值',
        getString2_key: '键',
        getString2_defValue: '缺省值',
        getStringMandatory: '通过键得到字符型管道值,找不到值时报错',
        getStringMandatory_key: '键',
        getInteger1: '通过键得到整数型管道值,不存在返回null',
        getInteger1_key: '键',
        getInteger2: '通过键得到整数型管道值,带缺省值',
        getInteger2_key: '键',
        getInteger2_defValue: '缺省值',
        getBoolean1: '通过键得到布尔型管道值,不存在返回null',
        getBoolean1_key: '键',
        getBoolean2: '通过键得到布尔型管道值,带缺省值',
        getBoolean2_key: '键',
        getBoolean2_defValue: '缺省值',
        containsKey: '判断给定键是否存在',
        containsKey_key: '键'
      },
      mevelConfig: {
        u: '常用函数库',
        p: '管道',
        event: '事件'
      },
      commonConditions: {
        lastError: '上一节点执行失败(错误处理方式为忽略)',
        lastSuccess: '上一节点执行成功',
        lastTrue: '上一节点返回true',
        lastFalse: '上一节点返回false',
        stringEqual: '管道字符串值等于(替换xyz和abc)',
        stringNotEqual: '管道字符串值不等于(替换xyz和abc)',
        integerEqual: '管道整数值等于(替换123和abc)',
        integerNotEqual: '管道整数值不等于(替换123和abc)'
      },
      processError: {
        label: '插入流程运行出错信息',
        options: {
          _errorProcess: '流程名称',
          _errorProcessId: '流程编号',
          _errorNode: '出错节点',
          _errorTime: '出错时间',
          _errorInfo: '错误基本信息',
          _errorDetail: '错误详细信息',
          _errorDetailSingle: '错误详细信息(单行)',
          _errorDetailCause: '错误根源',
          _errorHappen: '是否出错(布尔型)'
        }
      }
    },
    index: {
      title: '脚本编辑器',
      NoneScript: '非脚本',
      PipelineScript: '管道值',
      VariableScript: '变量',
      MvelScript: 'Mvel脚本',
      JoddScript: 'Jodd模板',
      test: '测试'
    },
    JoddMethodBuilder: {
      title: '添加方法代码',
      pipeline: '管道',
      event: '事件',
      pipelineKey: '管道键',
      eventKey: '事件键'
    },
    JoddScript: {
      addMethod: '添加方法代码',
      scriptArea_placeholder: '请输入脚本'
    },
    MvelMethodBuilder: {
      title: '添加方法代码'
    },
    MvelMethodBuilderDetail: {
      title: '方法参数设置',
      noPara: '此方法不需要设置参数,直接点击确定插入'
    },
    MvelScript: {
      addMethod: '插入方法代码',
      addString: '插入字符串',
      scriptArea_placeholder: '请输入脚本',
      addString_prompt: '请输入要添加的字符串',
      commonCondition: '常用流程连接条件'
    },
    PipelineScript: {
      applyValue_placeholder: '请输入管道键'
    },
    ScriptTest: {
      title: '脚本测试',
      test: '测试',
      testFinished: '计算完成'
    },
    ScriptTestData: {
      pipelineEnable: '模拟管道数据',
      pipeline: '管道数据',
      eventEnable: '模拟事件数据',
      event: '事件数据',
      script: '脚本',
      resultType: '结果类型',
      resultContent: '结果'
    },
    VariableScript: {
      applyValue_placeholder: '请选择变量'
    }
  },
  _util: {
    PayloadViewerTable: {
      viewPaload: '报文查看',
      error_no_payload: '没有报文下载'
    },
    ShowPayloadDialog: {
      replaceToLF: '替换此字符为换行',
      autoWrapLine: '行过长时自动换行',
      viewPaload: '报文查看',
      replacer_1: '不替换',
      replacer_2: "'(单引号)",
      replacer_3: '`(反单引号)',
      replacer_4: '+(加号)',
      beautifyOutput: '格式化数据',
      payloadSize: '报文大小'
    }
  },
  navbar: {
    welcome: '欢迎使用 Cloud Hub 5 管理后台',
    dashboard: '首页',
    profile: '个人中心',
    logout: '退出登录'
  },
  tagsView: {
    refresh: '刷新',
    close: '关闭',
    closeOthers: '关闭其它',
    closeAll: '关闭所有'
  },
  login: {
    title: '系统登录',
    login: '登录',
    username: '账号',
    password: '密码',
    error_no_username: '请输入用户名',
    error_no_password: '请输入密码',
    error_login_fail: '用户名或密码错误,请重新登录',
    change_password: '修改密码',
    error: {
      UnknownAccount: '用户名不存在',
      LockedAccount: '账号锁定',
      CredentialExipred: '账号密码过期,请修改密码',
      ExcessiveAttempts: '超过允许登录次数账号锁定',
      IncorrectCredentials1: '密码错误.<br>时间区间:',
      IncorrectCredentials2: '秒,已重试',
      IncorrectCredentials3: '次,允许重试',
      IncorrectCredentials4: '无限',
      IncorrectCredentials5: '次.<br>注意超过重试次数账号会被锁定'
    }
  },
  dashboard: {
    auditToday: '今日单量',
    auditTodaySuccess: '今日成功',
    auditTodayFail: '今日失败',
    eventToday: '今日警告',
    auditByStatus: '今日单量(状态)',
    auditByType: '今日单量(类型)',
    auditByDomain: '今日单量(域)',
    auditByServer: '今日单量(服务器)',
    event_title: '最新的报警信息',
    event_all: '查看全部',
    auditDailyConfig_title: '日单量',
    auditDailyConfig_auditToday: '今日',
    auditDailyConfig_auditYesterday: '昨日',
    auditLast14Config_title: '最近14天单量',
    auditTop10Transaction: '今日交易量排名前10',
    auditTop10TimeCost: '今日耗时排名前10(毫秒)'
  },
  audit: {
    infoName: '自定义',
    CheckpointDialog_title: '查看检查点',
    ColManDialog_title: '管理查询结果显示列',
    ColManDialog_choosed: '选择的的列',
    ColManDialog_unchoosed: '未选择的列',
    timeCost: '流程耗时(毫秒)',
    sourceBefore: '入口耗时(毫秒)',
    payloadSize: '报文大小',
    processInstanceId: '流程实例编号',
    process: '流程',
    sourceType: '来源类型',
    sourceName: '来源',
    errorInfo: '错误信息',
    errorNode: '出错节点',
    errorTime: '出错时间',
    retryTime: '重试时间',
    channel: '通道',
    strategy: '执行策略',
    server: '执行服务器',
    sourceServer: '入口服务器',
    createTime: '创建时间',
    lastTime: '结束时间',
    sourceTime: '接收时间',
    status_fail: '执行失败',
    status_retry: '正在重试',
    status_run: '正在运行',
    status_abort: '中断执行',
    status_wait: '等待',
    status_success: '成功',
    sourceType_entry: '入口',
    sourceType_async: '异步执行',
    sourceType_process: 'HTTP流程入口',
    sourceType_pre: 'HTTP预处理',
    sourceType_post: 'HTTP后处理',
    sourceType_fork: '流程分叉',
    preHandler: '(特殊)预处理',
    postHandler: '(特殊)后处理',
    _api_audit_id: 'API信息',
    criteria: {
      tip: '字符串字段可使用竖线分割多个查询条件,还可以使用星号匹配多个字符.本软件其他查询同样支持此特征.',
      hist: '数据库',
      sortBy: '排序字段',
      sortType: '排序方式',
      sortType_asc: '升序',
      sortType_desc: '降序',
      savedCriteria: '保存的查询',
      savedCriteria_button: '保存查询条件',
      customize_field_button: '自定义字段名',
      createTimeStart: '开始时间从',
      createTimeEnd: '开始时间到',
      pickerOptions_today: '今天',
      pickerOptions_yesterday: '昨天',
      pickerOptions_8pm_yesterday: '昨晚8点',
      pickerOptions_one_week: '一周前',
      pickerOptions_begin_this_month: '本月初',
      infoCustomizeConfig_title: '设置自定义字段名称',
      delCriteria_confirm: '是否删除保存的查询',
      retryFlag: '重试标记',
      retryFlag_all: '全部',
      retryFlag_yes: '有过重试',
      retryFlag_no: '没有重试',
      timeCostFrom: '耗时从(毫秒)',
      timeCostTo: '耗时到(毫秒)'
    },
    detail: {
      info: '基础',
      previous: '上一条',
      next: '下一条',
      downloadPayload: '下载报文',
      viewPayload: '查看报文',
      checkpoint: '检查点',
      checkpoint_start: '开始',
      checkpoint_process: '流程',
      checkpoint_user: '用户',
      customize: '自定义字段和报文',
      log: '日志',
      detail: '详情',
      startTime: '开始时间',
      content: '内容',
      relatedValue: '相关值',
      extra: '附加信息',
      timeCost: '耗时(毫秒)',
      pis: '调度流程',
      log_entry: '接收数据',
      log_entry_from: '来自',
      log_abort: '流程中断',
      log_abort_sys: '系统流程中断(重启后自动恢复)',
      abort_prehandler: '流程预返回false,处理中断',
      log_start: '流程开始',
      log_success: '流程成功',
      log_retry: '流程重试',
      log_fail: '流程失败',
      log_forceExit: '流程退出',
      log_unexpected: '流程异常错误',
      log_preHandler: '流程预处理',
      log_postHandler: '流程后处理',
      extra_retry: '重试',
      extra_ignore: '忽略错误',
      extra_forceExit: '退出',
      extra_isSkip: '跳过',
      extra_reentry_wait: '重入等待',
      extra_reentry_EVENT: '重入事件',
      extra_reentry_TIMEOUT: '重入超时',
      extra_join_wait: '结合等待',
      extra_join_continue: '结合继续',
      extra_desc_retry: '此步骤出错了,根据重试规则重试',
      extra_desc_ignore: '此步骤出错了,但是节点设置为忽略错误继续',
      extra_desc_forceExit: '此步骤的操作要求退出流程',
      extra_desc_isSkip: '此步骤没有激活,被跳过了',
      extra_desc_reentry_wait: '此步骤为重入,暂停等待外部事件或超时',
      extra_desc_reentry_EVENT: '此步骤为重入,接收到了外部事件继续',
      extra_desc_reentry_TIMEOUT: '此步骤为重入,没有接收到外部事件超时了,继续',
      extra_desc_join_wait: '此步骤为流程结合,不是所有分叉流程都执行完,等待',
      extra_desc_join_continue: '此步骤为流程结合,所有分叉都完成,继续',
      log_return: '返回值',
      error_detail: '错误明细',
      error_no_payload: '没有报文下载'
    },
    index: {
      criteria: '查询条件',
      result: '查询结果',
      detail: '数据详情'
    },
    result: {
      fetchData: '刷新',
      fetchData_description: '根据当前条件刷新数据',
      restart: '重启',
      restart_description: '从头开始执行,需要记录了开始检查点',
      resume: '继续',
      resume_description: '从出错的节点执行,需要记录了流程检查点',
      checkpoint: '检查点',
      checkpoint_description: '从用户自定义检查点开始执行',
      stop: '停止',
      stop_description: '停止正在运行或等待运行的流程',
      stopCluster: '集群停止',
      stopCluster_description:
        '集群环境下,管理界面连接服务器和流程运行服务器可能不同.本按钮试图获取并往流程运行服务器发送停止请求. 存在多条数据时只会试图从第一条数据获取目标服务器信息.',
      stopCluster_error: '获取目标服务器失败,无法发送请求',
      downloadPayload: '下载报文',
      downloadPayload_description: '下载接收到的报文',
      exportExcel: '下载表格',
      exportExcel_description: '下载下面表格内容',
      colSettingMan: '管理显示列',
      colSettingMan_description: '设置下面的列表显示哪些字段',
      resetColSetting: '重置显示列',
      resetColSetting_description: '把显示列设置成缺省的列',
      tagAndComment: '自定义标记和备注',
      tagAndComment_description: '譬如可以用于标记此数据的处理情况,也可以灵活用于其他目的',
      tag: '自定义标签',
      comment: '自定义备注',
      tag_none: '无自定义标签',
      tagAndComment_set_success: '设置自定义标签和备注成功',
      error_get_data: '获取业务数据失败',
      operate_prompt1: '请选择要',
      operate_prompt2: '的数据行',
      msg_text1: '成功,共有',
      msg_text2: '条,成功',
      msg_text3: '条',
      msg_text4: '允许',
      msg_text5: '不允许',
      msg_success: '成功',
      msg_fail: '失败',
      error_not_select: '请选择数据行',
      confirm_reset: '确定要重置显示列?'
    },
    SaveCriteriaDialog: {
      title: '保存查询条件',
      overwrite: '覆盖',
      name_placeholder: '请输入名称',
      error_no_name: '请输入名称',
      error_no_criteria: '请选择要覆盖的条件'
    }
  },
  alert: {
    insertTime: '记录时间',
    type: '类型',
    source: '来源',
    sourceSub: '详细来源',
    content: '内容',
    exceptionHandler: '主动报警',
    AlertDialog: {
      title: '查看报警详情',
      basic: '基本信息',
      exception: '错误详情',
      infos: '相关字段',
      payloads: '相关报文',
      data: '流程数据',
      pipeline: '流程管道'
    },
    data: {
      hist: '数据库',
      type_options: 'service:服务,process:流程,system:系统',
      timeStart: '时间开始',
      timeEnd: '时间结束',
      exportExcel: '下载表格'
    }
  },
  cluster: {
    index: {
      setting: '设置',
      performance: '性能',
      memory: '内存',
      threads: '线程',
      log: '日志',
      service: '服务',
      dispatch: '调度',
      license: '授权',
      msg_choosed: '下面标签显示的数据属于服务器',
      msg_clean_mem1: '内存清理成功.<br>清理前剩余:',
      msg_clean_mem2: 'M<br>清理后剩余:',
      title_setting: '服务器信息'
    },
    data: {
      gc: '内存清理',
      showInfo: '服务器信息',
      tableTitle: '服务器列表',
      server: '实例',
      name: '服务器名称',
      port: '端口',
      insertTime: '加入集群时间',
      lastTime: '最后心跳时间'
    },
    performance: {
      cpuUsageConfig: {
        user: '用户',
        sys: '系统'
      },
      memUsageConfig: {
        title: '内存(M)',
        globalTotal: '最大物理内存',
        globalUsed: '物理内存占用',
        vmTotal: '最大虚拟机内存',
        vmUsed: '虚拟机内存占用'
      }
    },
    memory: {
      categoryShow: '显示分类',
      memory_usage: '当前占用',
      memory_peakUsage: '峰值占用',
      unit: '单位',
      unit_b: '字节',
      unit_k: '千字节',
      unit_m: '兆字节',
      title_heapAndNoneHeap: '堆和非堆',
      data: {
        name: '名称',
        init: '初始值',
        used: '使用值',
        committed: '提交值',
        max: '最大值',
        usedPercentage: '使用百分比(%)',
        manager: '管理器'
      },
      title_directPool: '直接内存访问',
      data1: {
        name: '名称',
        count: '数量',
        memoryUsed: '使用量',
        totalCapacity: '总量'
      },
      title_collectors: '垃圾回收',
      data2: {
        name: '名称',
        count: '次数',
        time: '耗时(毫秒)',
        memoryPools: '内存池'
      }
    },
    thread: {
      checkLocked: '检查线程死锁',
      checkLockedTitle: '检查线程死锁结果',
      checkLockedNone: '没有被锁定的线程',
      lineNumber: '行号',
      methodName: '方法',
      className: '类',
      empty: '空',
      threadName: '线程名',
      threadGroup: '线程组',
      priority: '优先级',
      state: '状态',
      group: '线程组',
      trace: '运行位置',
      dumpHeapButton: '内存堆转储下载',
      dumpHeapPrompt:
        '确定要生成内存堆转储并下载吗?此动作可能比较耗时,如超时请在对应服务器的tmp目录查看.下载文件可以通过工具,如Eclipse Memory Analyzer分析!'
    },
    log: {
      dialog_title: '日志数据',
      size: '大小',
      lastModified: '更新时间'
    },
    service: {
      confirm_prompt: '是否确认',
      config: '配置',
      status: '状态',
      category: '分类',
      implClass: '实现类',
      depends: 'depends',
      autoStart: '自动启动',
      category_options: 'SYS:系统,USER:用户'
    },
    dispatch: {
      manage: '管理',
      priority: '优先级',
      concurrent: '最大并发',
      running: '运行',
      waiting: '等待',
      removeSel: '删除选中',
      removeAll: '删除全部',
      moveSel: '移动选中',
      moveAll: '移动全部',
      data: {
        time_entry: '时间',
        subject: '标题',
        source_server: '来源服务器',
        source_type: '来源类型',
        source_name: '来源名称',
        content: '内容'
      },
      waitingMsg: '等待消息',
      move_prompt: '选择移动目标通道',
      targetChannel: '目标通道',
      operate_text1: '确定要',
      operate_text2: '移动',
      operate_text3: '删除',
      operate_text4: '全部',
      operate_text5: '选中',
      operate_text6: '消息?<br>此操作不可逆且可能丢失数据,慎重操作.<br>如不清楚请与管理员联系!',
      error_no_data: '请选择要处理的数据行',
      success_text1: '处理成功,共',
      success_text2: '条',
      resetChannel_confirm:
        '确定重置通道所有请求的正在运行计数吗?重置计数后通道能够处理允许最大数量的请求',
      resetChannel_result: '重置成功'
    },
    license: {
      request: '申请',
      request_description: '点击申请并把保存的文件发送给授权生成方',
      title: '证书申请',
      customer: '客户',
      remark: '备注',
      period: '过期天数',
      msg_success: '授权成功,到期时间',
      msg_fail: '授权失败,原因'
    }
  },
  various: {
    index: {
      runningScheduler: '运行的定时器',
      activeScheduler: '激活的定时器',
      activePool: '连接池',
      entityPara: '运行时参数'
    },
    runningScheduler: {
      description: ' 列出在各个节点上正在运行的定时任务.',
      del_prompt:
        '删除可能会导致多个实例执行一个定时任务,除非由于数据库不可用导致的问题,不建议删除.是否继续?',
      keyGroup: '组',
      keyDescription: '名称',
      instanceId: '服务器',
      time: '开始运行时间'
    },
    activeScheduler: {
      state: '状态',
      keyGroup: '组',
      description: '名称',
      startTime: '开始时间',
      endTime: '结束时间',
      previousFireTime: '上次运行时间',
      nextFireTime: '下次运行时间'
    },
    activePool: {
      trial: '测试',
      trial_prompt: '测试会试图尝试创建池中对象并立刻释放,用于测试配置是否可用?',
      trial_text1: '池顺利创建对象',
      trial_text2: '池实现不支持测试操作',
      trial_success: '测试成功',
      trial_fail: '测试失败',
      monitor_settingMax: '设置最大值',
      monitor_settingIdle: '设置空闲值',
      monitor_actualMax: '实际最大值',
      monitor_actualIdle: '实际空闲值'
    },
    entityPara: {
      error_type: '无法识别的数据类型,无法修改数据'
    }
  },
  entry: {
    trial: '执行一次',
    trial_confirm: '你确定要执行一次?注意在集群环境下,定时器也可能启动执行导致潜在的冲突.',
    meta: '元数据',
    meta_description: '元数据会自动加入到管道中\n处理流程能够读取到设置值'
  },
  process: {
    ActionDialog: {
      exceptionHandler: '错误处理',
      exceptionHandler_options: 'escalate:抛出错误,ignore_alert:忽略并报警,ignore:忽略不报警',
      logMode: '日志模式',
      logMode_description:
        '设置操作记录日志的目标.系统日志指统一记录在系统标准日志中;独立日志指每个操作单独记录,可以在业务数据界面查看',
      logMode_options: 'none:不记录,system:系统日志,solo:独立的日志',
      logLevel: '日志级别',
      logLevel_options: 'ERROR,WARN,INFO,DEBUG',
      sequence: '序号',
      filename: '下载文件名',
      infoTabConfig: '流程参数',
      infoTabConfig_description:
        '设置后会显示在业务数据界面,方便用户搜索查询\n一般设置重要的数据,如订单编号等',
      payloadTabConfig: '报文参数',
      payloadTabConfig_description: '设置后会显示在业务数据界面\n一般用于显示经过转换后的报文'
    },
    Designer: {
      return_confirm: '流程已经被修改,是否确定不保存退出?'
    },
    FieldSet: {
      infoTableConfig: '附加字段',
      sequence: '序号',
      format: '格式',
      format_options: 'string:字符串,json:JSON,list:列表'
    },
    Panel: {
      initConnConfig: {
        title: '连接属性',
        condition: '条件',
        priority: '优先级'
      },
      detach_confirm: '此操作将永久删除该连接, 是否继续?'
    },
    Pallet: {
      filter_placeholder: '请输入过滤条件'
    },
    ProcessNode: {
      startNode: '设为启动节点'
    },
    Toolbar: {
      returnList: '返回列表',
      processInfo: '流程信息'
    }
  },
  pool: {
    poolEnabled: '使用池'
  },
  mapping: {
    deploy: '上传部署',
    deploy_text1: '映射列表,共',
    deploy_text2: '条',
    deploy_text3: '没有部署映射',
    deploy_text4: '异常列表,共',
    deploy_text5: '没有异常',
    deploy_result: '部署结果',
    sourceMessageType: '源类型',
    sourceMessagePath: '源消息',
    destMessageType: '目标类型',
    destMessagePath: '目标消息',
    show: {
      button: '查看',
      messageSource: '源消息结构',
      messageSourceConfig: '源消息设置',
      messageDest: '目标消息结构',
      messageDestConfig: '目标消息设置',
      code: '代码',
      info: '基本信息',
      path: '路径',
      event: '事件'
    }
  },
  runtimeStrategy: {
    strategyRetry: '重试',
    strategyPersistent: '持久化',
    strategyLog: '日志',
    strategyCheckpointStart: '开始检查点',
    strategyCheckpoint: '执行检查点',
    strategyExceptionDump: '异常DUMP',
    strategyDebugMode: '调试模式',
    informEvent: '出错时报警',
    strategyRetry_options: 'RETRY:重试,IGNORE:忽略',
    strategyPersistent_options:
      'END:结束时,START_END:开始和结束时,NONE:不,ALL:每步结束,ON_ERROR:出错时',
    strategyLog_options: 'ALL:全部记录,NONE:不记录,SMART:记录重要',
    strategyCheckpointStart_options:
      'SAVE_DELETE:开始记录成功删除,NO_SAVE:不保存,ALWAYS_SAVE:一直保存',
    strategyCheckpoint_options: 'SAVE_ERROR:出错时记录,NO_SAVE:不保存,ALWAYS_SAVE:一直保存',
    strategyExceptionDump_options: 'SIMPLE:简单,FULL:完整'
  },
  channel: {
    persistent: '持久化',
    priority: '优先级',
    concurrent: '并发数',
    defaultChannel: '缺省',
    submitMode: '提交模式',
    submitMode_desc:
      '经典模式下所有异步请求会进入队列等待调度;\n智能模式下请求时当通道有空闲线程时会立刻执行,否则回归到经典模式处理',
    submitMode_options: 'CLASSIC:经典模式,SMART:智能模式',
    bindingServers: '绑定服务器',
    bindingServers_desc:
      '不设置时所有服务器都适用此通道;\n设置后只有指定的服务器会适用此通道.\n设置方式为逗号分隔的服务器实例编号.',
    mode: '模式',
    mode_desc: '缺省为本地.远程使用mongoDB协调',
    mode_options: 'LOCAL:本地,GLOBAL:全局'
  },
  variable: {
    expression: '表达式'
  },
  cache: {
    data_unchoose: '请选择要查看的缓存定义'
  },
  lookup: {
    data_unchoose: '请选择要编辑的查照表定义',
    clear_confirm: '是否确认清除所有数据?',
    clear_text1: '共清除',
    clear_text2: '条'
  },
  authentication: {
    data_unchoose: '请选择',
    principal: '认证主体(用户名)',
    credential: '认证密钥(密码)',
    dialog_title: '认证库数据管理',
    dialog_basic: '认证信息',
    dialog_authorization: '权限信息'
  },
  authorization: {
    data_unchoose: '请选择要编辑的授权库定义',
    dialog_title: '授权库数据管理',
    sequence: '顺序',
    public: '无需授权即可访问',
    resources: '资源',
    method: '类型',
    method_options: 'ALL:全部,GET:GET,POST:POST',
    pattern: '匹配正则表达式',
    props: '附加属性'
  },
  solutionDm: {
    solution: '解决方案',
    auth: '外部系统',
    auth_description: '选中授权本API能够访问的外部系统后保存',
    interceptor: '拦截器',
    related: '相关',
    doc: '文档',
    project: '项目',
    pleaseChoose: '请选择解决方案',
    top: {
      importOas3: ' 导入OAS3文件',
      apiList: 'API列表',
      apiDevelop: 'API开发',
      tag: 'API标签',
      externalSystem: '外部系统',
      schema: '数据模型',
      portalUser: '门户用户',
      interceptor: '拦截器'
    },
    data: {
      title: 'API项目',
      basic: '基本',
      authentication: '认证',
      audit: '监控',
      portal: '门户',
      interceptor: '拦截器',
      name_description:
        '用于标识此数据中台<br>也用于生成的HTTP入口、认证库、授权库、缓存以及获取令牌流程的名称',
      description_description: '也用于生成的HTTP入口、认证库、授权库、缓存以及获取令牌流程的描述',
      entryHttpHost: '绑定IP',
      entryHttpHost_description:
        '发布的API会在此端口被访问<br>可以绑定在某IP上或所有IP(空)<br>更复杂设置,如HTTPS,直接在生成的HTTP入口里设置',
      entryHttpPort: '绑定端口',
      urlExternal: '访问URL',
      urlExternal_description: '外部访问此接口的URL.没有填写时使用前面的IP和端口拼接',
      uriMatchMode: 'URI匹配方式',
      uriMatchMode_options: 'STARTWITH:开头匹配,EQUAL:相等,SPRING:Spring MVC模式',
      generatePathMode: '实现方式',
      generatePathMode_description:
        '说明API如何生成在HTTP入口的路径中\n传统模式下每个API生成一个路径\n高级模式下只会生成一个服务,处理所有API',
      generatePathMode_options: 'legacy:传统模式,advanced:高级模式',
      approvalMode: '审批方式',
      approvalMode_description:
        '缺省不启用审批方式\r\n启用后新增/修改/删除/禁用都需要经过审批后才能上线.',
      approvalMode_options: 'no:不启用,all:启用',
      errorHandler: {
        label: '错误处理',
        processErrorHandler: '错误处理方式',
        processErrorHandler_options:
          'ERROR500:500报错,IGNORE:忽略错误流程给出反馈,CUSTOMIZE:自定义,PROCESS:流程',
        pehCustomizeCode: '出错HTTP代码',
        pehCustomizeContentType: '出错Content type',
        _ie_pehCustomizeContent: '出错HTTP内容',
        _ie_pehCustomizeContent_description: '此处会在流程执行完成后计算表达式',
        pehProcess: '出错处理流程',
        pehRuntimeStrategy: '出错执行策略'
      },
      auditable: '监控',
      auditable_description: '是否记录API监控日志',
      authType: '认证方式',
      authType_options: 'none:不认证,oauth2:Open Auth 2,basic:HTTP基本认证',
      realm: '安全域',
      tokenUri: '获取凭证URI',
      tokenLength: '凭证长度',
      tokenExpiresIn: '凭证过期时间(秒)',
      tokenGenMode: '凭证生成方法',
      tokenGenMode_options: 'NONE:无限制,REUSE:重用',
      tokenReuseThreshhold: '凭证重用阈值(秒)',
      tokenReuseThreshhold_description:
        '保存的凭证过期时间和当前时间差值小于此值时会被重用,值必须大于等于0',
      cacheType: '缓存类型',
      cacheType_description:
        '存储认证后的凭证<br>Ehcache本地存储,Redis为远程存储<br>当需要在多台服务器间共享凭证时使用redis',
      cacheType_options: 'ehcache:Ehcache(本地),redis:Redis(远程)',
      cacheEhcacheHeap: '堆内内存(个)',
      cacheEhcacheHeap_description: '内存中存放的对象个数',
      cacheRedisConn: 'Redis连接',
      cacheExpireTti: '空闲超时TTI(秒)',
      cacheExpireTti_description: '缓存对象从最后访问时间开始计算,超过此时间阈值过期',
      cacheExpireTtl: '最大寿命TTL(秒)',
      cacheExpireTtl_description: '缓存对象从放入时间开始计算,超过此时间阈值过期',
      auditRequestBody: '记录请求体',
      auditRequestBodyMaxSize: '最大记录字节',
      auditRequestEncoding: '请求体编码',
      auditResponseBody: '记录应答体',
      auditResponseBodyMaxSize: '最大记录字节',
      auditResponseEncoding: '应答体编码',
      auditRequestHeaders: '记录请求头',
      auditRequestHeadersList: '请求头列表',
      auditRequestHeadersList_description: '用逗号分隔的头名称列表.\r\n空代表记录所有头信息.',
      auditResponseHeaders: '记录应答头',
      auditResponseHeadersList: '应答头列表',
      auditResponseHeadersList_description: '用逗号分隔的头名称列表.\r\n空代表记录所有头信息.',
      auditQueryString: '记录查询参数',
      auditInterceptors: '记录拦截器执行',
      auditProcess: '记录流程信息',
      portalJWTSecret: 'JWT密钥',
      portalJWTSecret_description: '如果没有设置系统会自动生成随机值',
      api: {
        name_description: 'API名称,也生成的流程的名称',
        description_description: 'API描述,也用于生成的流程的描述',
        apiType: '类型',
        apiType_options: 'DB:数据库,SAP_RFC:SAP RFC,MAPPING:映射,FORWARD:HTTP请求转发,PROCESS:流程',
        method: '方法',
        method_options:
          '*:全部,GET,POST,PUT,DELETE,OPTIONS,HEAD,CONNECT,TRACE,PATCH,MOVE,COPY,LINK,UNLINK,WRAPPED',
        uri: '访问URI',
        uri_description: '用户使用此URI访问API<br>以 / 开头,不以 / 开头保存系统时系统会自动加入 /',
        apiErrorHandler: '方式',
        apiErrorHandler_options: 'inherit:继承自项目配置,specific:API自定义',
        //sqlMode:'类型',
        mapping: {
          tabName: '输出',
          contentType: 'Content type',
          content: '内容'
        },
        process: {
          contentToPayload: '保存HTTP请求体',
          processErrorHandler: '错误处理方式',
          processErrorHandler_options: 'ERROR500:500报错,IGNORE:忽略错误流程给出反馈'
        }
      },
      infoTabConfig: {
        title: '流程参数',
        description:
          '设置后会显示在业务数据界面,方便用户搜索查询\n' +
          '一般把流程中重要的数据设置在附加字段里\n' +
          '注意:附加字段1-4系统占用,请勿使用'
      }
    },
    interceptorChooser: {
      saveToSet: '保存后可设置拦截器.',
      tips: '请从左边选择拦截器,注意系统会按照顺序执行. 预处理会按照API,分组和解决方案的顺序执行,后处理刚好相反.相同的拦截器后面的会自动忽略不执行.'
    },
    OpenApiViewer: {
      title: 'Open API查看',
      tab_view: '查看调试',
      tab_code: 'Open API代码'
    },
    Oas3Upload: {
      title: '导入OAS3格式的文件',
      itemLabel: 'OAS3文件',
      choose_file: '选取文件',
      clear_file: '清除文件',
      submitUpload: '开始上传',
      submitUpload_tip: '请选择要上传到文件,一次只允许传一个',
      prefixMatch: '匹配前缀(外部系统调用API时加入此前缀,如/v2)',
      prefixTarget: '后端系统前缀(调用后端系统时会自动在服务器IP接口后加入此前缀)',
      override: '是否覆盖(选择后会自动覆盖已经存在的数据)',
      noFile: '没有选择数据,无法上传',
      uploadSuccess: '上传成功',
      uploadFail:
        '版本错误,只支持OAS3格式的JSON文件.其他版本可以使用工具,如 https://editor.swagger.io/ 转换成OAS3格式',
      uploadFail_unkown: '上传返回无法识别的代码'
    },
    ApiSchema: {
      tip: '此处数据定义的数据模型可以被本解决方案内所有的API共享使用.'
    },
    ApiDummy: {
      description: '没有选择解决方案,请点击下面按钮',
      addProject: '增加API项目'
    },
    ApiList: {
      version: '版本',
      tag: '标签',
      buttonsConfig: {
        batchBorn: '批量生成OAS',
        batchDelete: '批量删除',
        addAPI: '增加API'
      },
      versionConfig: {
        version: '版本号',
        version_description: '由用户设置系统不做校验',
        switchVersion: '切换版本',
        versionTime: '生成时间'
      },
      apiVersion: {
        title: 'API版本',
        tab_add: '新增版本',
        tab_history: '历史版本',
        tip_add: '把当前状态快照保存成版本,需要时可以在历史版本中切换',
        tip_history: '列出当前API的历史版本.点击版本切换可以切换回选择版本.',
        switch_confirm: '确定要切换当前版本到此版本吗?切换后立刻生效且无法回溯!',
        switch_success: '版本切换成功.请刷新API列表查看最新数据.',
        delete_confirm: '确定要删除此版本?删除后无法回溯!',
        delete_success: '删除版本成功'
      }
    },
    schemaEditor: {
      request: {
        in: '位置',
        required: '必须'
      },
      MultipleItemsEditor: {
        detailBack: '不保存直接返回列表',
        detailSave: '保存后返回列表',
        valdiateFail: '数据校验失败,请检查'
      },
      OpenApiEditorResponseDetail: {
        httpCode: 'HTTP代码',
        responseBody: '反馈报文'
      },
      PropertyEditor: {
        sample: '样例',
        subType: '子类型',
        enum: '枚举',
        match: '匹配',
        match_placeholder: '正则表达式',
        minLength: '最小长度',
        maxLength: '最大长度',
        minimum: '最小值',
        maximum: '最大值',
        multipleOf: '倍数',
        namePara: '参数名'
      },
      PropertyListEditor: {
        addProperty: '增加属性'
      },
      OpenApiEditor: {
        title: 'Open API编辑',
        buttons: {
          born: '重新生成OAS',
          saveClose: '保存后关闭'
        },
        tabs: {
          requestPara: '输入参数',
          requestBody: '输入报文',
          requestBody_tip: '输入报文在HTTP方法为GET和HEAD时无意义.',
          response: '输出',
          schema: '数据模型',
          oadData: 'OAS数据',
          oadData_tip1: '请首先保存后点击.',
          oadData_tip2: '刷新.',
          oadData_tip3: '得到最新的OAS数据.单击',
          oadData_tip4: '下载',
          oadData_tip5: '输入报文在HTTP方法为GET和HEAD时无意义.',
          oasTrial: 'OAS调试.'
        },
        loadSuccess: '数据加载成功',
        born_prompt1: '确定要重新生成OAS',
        born_prompt2: '此API支持自动生成OAS,是否自动生成?',
        bornSuccess: '数据生成成功',
        confirm_close: '数据已经修改,是否不保存就退出?'
      }
    }
  },
  solutionRms: {
    title: '收转发解决方案',
    related: '相关',
    receiveType: '接收方式',
    receiveType_description: '选择从外部系统接收数据的方式.<br>选择不同方式具有不同的配置参数.',
    mappingType: '转换方式',
    mappingType_description: '如果选择没有映射,会发送接收到的数据<br>否则发送映射后数据',
    sendType: '发送方式',
    sendType_description: '选择把数据写外部系统的方式.<br>选择不同方式具有不同的配置参数.',
    content: '说明',
    content_description:
      '收转发应用场景为从外部系统通过各种协议接收数据,经过格式转换(也可以不转换)后通过各种协议发送给外部系统.<br>' +
      '通过配置入口和流程能够达到相同的结果,但是相对上手度会高一些,但是更加灵活和功能强大<br>' +
      '本配置完成会自动生成入口和流程,可以通过对应菜单查看和编辑',
    unsupport: '不支持',
    receiveUnsupport: '不支持选择的接收方式,可能是未安装指定模块或未授权使用',
    sendUnsupport: '不支持选择的发送方式,可能是未安装指定模块或未授权使用',
    as2_para_value:
      'm:"压缩:"+(p.get("COMPRESSED")==null?"未知":"true"==p.get("COMPRESSED")?"是":"否") +",加密:"+(p.get("ENCRYPTED")==null?"未知":"true"==p.get("ENCRYPTED")?p.get("ENCRYPT_ALGORITHM"):"否") +",签名:"+(p.get("SIGNED")==null?"未知":"true"==p.get("SIGNED")?p.get("SIGN_ALGORITHM"):"否")',
    receiveTypes: {
      filePollingService: '文件轮询',
      filePollingService_filePathFull: '读取文件',
      ftpPollingEntry: 'FTP轮询',
      ftpPollingEntry_ftpDownloadPath: '读取路径',
      sftpPollingEntry: 'SFTP轮询',
      sftpPollingEntry_sftpDownloadPath: '读取路径',
      as2ListenerService: 'AS2监听',
      as2ListenerService_as2PayloadFileName: '接收文件名',
      as2ListenerService_as2PayloadContentType: '接收文件类型',
      as2ListenerService_mic: '接收MIC',
      as2ListenerService_para: '接收AS2参数',
      as2ListenerService_as2MdnMode: '接收MDN类型',
      emailPollingService: '邮件轮询',
      emailPollingService_fileName: '接收文件名',
      dbPollingEntry: '数据库轮询',
      dbPollingEntry_dbCount: '接收数量',
      dbPollingEntry_dbSpec: '接收数据规格'
    },
    mappingConfig: {
      mapping: '映射',
      paraTable: '输入参数'
    },
    mappingTypes: {
      none: '不映射',
      mapping: '映射'
    },
    sendTypes: {
      none: '不写入任何系统',
      fileSend: '写入文件',
      fileSend_result: '写入文件名',
      ftpSend: 'FTP发送',
      ftpSend_result: '写入文件名',
      ftpSend_ftpUploadPath: '写入目录',
      sftpSend: 'SFTP发送',
      sftpSend_result: '写入文件名',
      sftpSend_sftpUploadPath: '写入目录',
      as2Send: 'AS2发送',
      as2Send_result: '发送AS2编号',
      as2Send_mic: '发送MIC',
      as2Send_para: '发送AS2参数',
      as2Send_as2MdnMode: '发送MDN类型',
      emailSend: '发送邮件',
      emailSend_result: '发送邮件编号',
      jmsSend: 'JMS发送',
      jmsSend_result: '发送JMS消息编号'
    },
    Wizard: {
      title: '收转发向导',
      Basic: '基础',
      Receive: '接收',
      Mapping: '转换',
      Send: '发送',
      Info: '流程参数'
    }
  },
  solutionAuth: {
    related: '相关',
    save_confirm: '继续此操作会重启HTTP服务并提示错误(请忽略),是否继续?重启后需要重新登录',
    title: '配置登录信息',
    adminApiPanel: '管理界面API设置',
    entryApiHost: '绑定IP',
    entryApiHost_description: '为了安全,一般绑定在127.0.0.1上,不允许外部直接访问',
    entryApiPort: '端口',
    entryApiPort_description: '仅仅内部调用,可以是任意端口',
    adminUiPanel: '管理界面前端访问设置',
    entryAccessHost: '绑定IP',
    entryAccessHost_description: '空代表绑定所有IP.',
    entryAccessPort: '端口',
    entryAccessPort_description: '管理界面的端口,缺省值为9999',
    useNewAdmin: '使用新界面',
    useNewAdmin_description: '可能需要清除浏览器缓存后才起效',
    portalPanel: 'API门户设置',
    portalEnabled: '启用',
    portalEnabled_description: '启用后才能访问API门户',
    portalAccessHost: '绑定IP',
    portalAccessHost_description: '空代表绑定所有IP.',
    portalAccessPort: '端口',
    portalAccessPort_description: 'API门户端口,缺省值为9998',
    i8nPanel: '多语种设置',
    i18nEnabled: '支持多语种',
    i18nDefault: '缺省语言',
    i18nDefault_options: 'zh:中文,en:英文',
    cachePanel: '登录缓存设置',
    cacheType: '缓存类型',
    cacheType_description:
      '存储认证后的凭证\nEhcache本地存储,Redis为远程存储\n当需要在多台服务器间共享凭证时使用redis',
    cacheType_options: 'ehcache:Ehcache(本地),redis:Redis(远程)',
    cacheEhcacheHeap: '堆内内存(个)',
    cacheEhcacheHeap_description: '内存中存放的对象个数',
    cacheRedisConn: 'Redis连接',
    cacheExpireTti: '空闲超时TTI(秒)',
    cacheExpireTti_description: '缓存对象从最后访问时间开始计算,超过此时间阈值过期',
    cacheExpireTtl: '最大寿命TTL(秒)',
    cacheExpireTtl_description: '缓存对象从放入时间开始计算,超过此时间阈值过期',
    sysCheckPanel: '系统健康检查',
    sysCheckInterval: '检查间隔(分钟)',
    checkChannel: '检查通道',
    channelThreshold: '报警阈值',
    checkProcess: '检查流程',
    processThreshold: '报警阈值(分钟)',
    checkCPUandMem: '检查CPU和内存',
    cpuThreshold: 'CPU占用率',
    memSystemThreshold: '系统内存占用率',
    memVMThreshold: '虚拟机内存占用率',
    housekeepingPanel: '数据转储设置',
    schedulerHK_description: '没有设置时数据转储会被禁用',
    auditHK_enabled: '转储业务数据',
    auditHK_filterThreshold: '保留天数',
    auditHK_histNameSuffix: 'Collection后缀',
    alertHK_enabled: '转储报警数据',
    alertHK_filterThreshold: '保留天数',
    alertHK_histNameSuffix: 'Collection后缀',
    payloadHK_enabled: '压缩报文',
    payloadHK_filterThreshold: '保留天数',
    accessLogHK_enabled: '转储访问日志数据',
    accessLogHK_filterThreshold: '保留天数',
    accessLogHK_histNameSuffix: 'Collection后缀',
    apiAuditHK_enabled: '转储API监控日志',
    apiAuditHK_filterThreshold: '保留天数',
    apiAuditHK_histNameSuffix: 'Collection后缀',
    accountSecurityPanel: '账户安全',
    passwordStrengthRequired: '密码强度要求',
    passwordStrengthRequired_description: '满分100,详细参考用户手册',
    passwordExpireDays: '密码过期天数',
    passwordExpireDays_description: '0代表密码永不过期',
    passwordChangeForNew: '新用户修改密码',
    loginCount: '登录重试次数',
    loginCount_description: '0代表可以一直重试.\n登录重试期内超过重试次数会锁定账号.',
    loginPeriod: '登录重试期间(秒)',
    loginPeriod_description: '在此时间窗口内统计重试次数',
    loginExpire: '登录过期期间(秒)',
    loginExpire_description: '超过此时间需要重新登录',
    accessLogEnabled: '记录访问日志',
    logLevelStrategy: '登录失败时显示完整错误信息',
    logLevelStrategy_description: '不显示完整错误信息可以提升安全性',
    auditInfoPanel: '业务数据自定义字段缺省设置',
    themePanel: '显示样式',
    navBarBackgroundColor: '顶部背景颜色',
    systemName: '系统名称'
  },
  reportFlow: {
    item: '项目',
    count: '数量',
    percentage: '占总数百分比',
    countError: '失败数量',
    errorRate: '失败率',
    mode_options:
      'status:状态,processName:流程,domain:域,sender:发送方,receiver:接收方,subject:标题,docType:单据类型,-month:月,-dayOfMonth:日,-hour:小时',
    timeStart: '时间开始',
    timeEnd: '时间结束'
  },
  reportPerformance: {
    item: '项目',
    count: '数量',
    countGood: '数量(处理时间小于60分钟)',
    percentage: '百分比',
    timeStart: '时间开始',
    timeEnd: '时间结束'
  },
  reportMonthCompare: {
    count: '数量',
    countLast: '上月数量',
    percentage: '增长百分比',
    year: '年份',
    month: '月份',
    mode_options:
      'status:状态,processName:流程,domain:域,sender:发送方,receiver:接收方,subject:标题,docType:单据类型'
  },
  reportTraffic: {
    count: '总条数',
    avgPayload: '平均报文大小',
    totalPalyoad: '总报文大小',
    avgTimeCost: '平均处理时间(毫秒)'
  },
  reportTrafficByStatus: {
    total: '总条数',
    SUCCESS: '成功',
    FAIL: '失败',
    ABORT: '中断',
    RUN: '执行中',
    RETRY: '重试中',
    errorRate: '错误率%'
  },
  reportHealth: {
    time: '时间区间',
    time_options: 'hour:小时,dayOfMonth:日,month:月,year:年',
    cpu_percent: 'CPU平均',
    cpu_percent_max: 'CPU最大',
    mem_percent: '内存平均',
    mem_percent_max: '内存最大',
    mem_vm_percent: '虚拟机内存平均',
    mem_vm_percent_max: '虚拟机内存最大',
    networkSpeedRecv: '接收速度平均',
    networkSpeedRecv_max: '接收速度最大',
    networkSpeedSent: '发送速度平均',
    networkSpeedSent_max: '发送速度最大'
  },
  event: {
    title: '报警设置',
    sequence: '顺序',
    mode: '触发模式',
    mode_options: 'SIMPLE:简单,CUMULATE:累计',
    maxCount: '最多累计次数',
    maxPeriod: '最长累计(秒)',
    criteriaType: '匹配方式',
    criteriaType_options: 'RULES:规则,SCRIPT:脚本',
    criteriaScript: '匹配脚本',
    criteriaRules: '匹配规则',
    criteriaRules_operator: '操作符',
    criteriaRules_operator_options:
      '=:等于,!=:不等于,in:包含,notIn:不包含,regex:正则表达式,isEmpty:空,notEmpty:不为空',
    operation: '报警操作'
  },
  deploy: {
    please_choose: ' 请选择要部署的文件',
    deploy_button: '点击上传部署',
    deploy_result: '导入结果',
    result_text0: '条',
    result_text10: '目录列表,共',
    result_text11: '没有部署目录',
    result_text20: '操作列表,共',
    result_text21: '没有部署操作',
    result_text30: '服务列表,共',
    result_text31: '没有部署服务',
    result_text40: '资源列表,共',
    result_text41: '没有部署资源',
    result_text50: '插件列表,共',
    result_text51: '没有部署插件',
    result_text70: 'Mapper方法目录,共',
    result_text71: '没有Mapper方法目录',
    result_text80: 'Mapper方法,共',
    result_text81: '没有Mapper方法',
    result_text60: '异常列表,共',
    result_text61: '没有异常',
    status_new: '新增',
    status_replace: '替换',
    status_ignore: '忽略',
    status_suppress: '禁用',
    suppress: '禁用',
    unsuppress: '取消禁用',
    suppressConfirm: '确定要禁用吗',
    unsuppressConfirm: '确定要取消禁用吗',
    tab_new: '新部署',
    tab_existing: '已部署',
    tab_suppress: '禁用的部署'
  },
  migration: {
    nameMatch: '匹配名称',
    please_choose: '请选择要导出的数据',
    choose_file: '选取文件',
    clear_file: '清除文件',
    view_file: '查看内容',
    migrate_file: '上传更新',
    migrate_success: '数据迁移成功',
    exportPanel: '配置导出',
    importPanel: '配置导入',
    please_input_filter: '请输入过滤字符串',
    choosed: '已选择',
    unchoosed: '未选择',
    configFolderMode: {
      label: '配置目录处理方式',
      option1: '保留原始值',
      option2: '缺省目录',
      option3: '使用当前值'
    }
  },
  uploadTest: {
    file: '文件',
    choose_file: '选取文件',
    clear_file: '清除文件',
    upload: '上传开始测试',
    upload_prompt: '请选择要上传到文件,允许一次上传多个',
    pipeline: '管道数据',
    error_no_file: '请选择文件',
    error_no_entry: '入口没有输入',
    test_success: '处理成功'
  },
  profile: {
    title: '密码修改',
    changePassword: '修改密码',
    username: '用户名',
    password: '原密码',
    newPassword: '新密码',
    newPassword1: '重复密码',
    password_invalid: '密码为数字，小写字母，大写字母，特殊符号 至少包含三种，长度为 8 - 30位',
    error_not_match: '新密码不相同',
    error_user_not_exist: '用户不存在',
    error_password_mismatch: '旧密码不匹配',
    error_password_strength: '新密码强度不够'
  },
  accessLog: {
    insertTime: '记录时间',
    data: {
      hist: '数据库',
      timeStart: '时间开始',
      timeEnd: '时间结束',
      url: 'URL',
      rh: '远程主机',
      ra: '远程IP',
      rp: '远程端口',
      ai: '用户ID',
      an: '用户名'
    }
  },
  dmAudit: {
    hist: '数据库',
    startTimeFrom: '开始时间从',
    startTimeTo: '开始时间到',
    dialog: {
      title: '显示API监视详情',
      basic: '基础',
      exception: '错误详情',
      requestBody: '请求体',
      requestHeaders: '请求头',
      responseBody: '应答体',
      responseHeaders: '应答头',
      interceptorResult: '拦截器执行',
      processInfo: '流程信息'
    },
    interceptorResult: {
      type: '类型',
      name: '名称',
      status: '状态',
      exception: '异常',
      type_prehandler: '预处理',
      type_posthandler: '后处理',
      status_success: '成功',
      status_fail: '失败',
      status_abort: '中断',
      detail: '异常详情'
    },
    data: {
      startTime: '开始时间',
      endTime: '结束时间',
      timeConsume: '耗时(毫秒)',
      direction: ' 方向',
      solution: '解决方案',
      solutionDescription: '解决方案描述',
      api: 'API',
      apiDescription: 'API描述',
      apiType: 'API类型',
      apiMethod: ' API方法',
      apiUri: 'API URI',
      queryString: '查询参数',
      authName: ' 认证名',
      authPrincipal: '认证主体',
      requestSize: '请求大小',
      responseSize: '反馈大小',
      url: '完整访问地址',
      rh: '远程主机',
      ra: '远程地址',
      rp: '远程端口',
      status: '状态',
      httpCode: 'HTTP码',
      server: '服务器',
      error: '错误详情'
    }
  },
  rn: {
    data: {
      owner: '自己',
      partner: '合作伙伴',
      pipCode: 'PIP代码',
      pipName: 'PIP名称',
      pipVersion: 'PIP版本',
      initiator: '初始者',
      rnifVersion: 'RNIF版本',
      pipInstanceId: 'PIP实例编号',
      globalUsageCode: '生产/测试',
      responseMode: '回执方式',
      pipType: 'PIP类型',
      signStrategy: '签名策略',
      signAlgorithm: '签名算法',
      encryptStrategy: '加密策略',
      encryptAlgorithm: '加密算法',
      datatransferEncoding: '传输编码',
      encoding: '编码',
      requestID: '请求消息编号',
      requestDate: '请求消息时间',
      requestCount: '请求发送/接收次数',
      requestLastTime: '请求最后时间',
      requestMessageDigest: '请求消息摘要',
      requestSignalID: '请求回执编号',
      requestSignalDate: '请求回执时间',
      responseID: '响应消息编号',
      responseDate: '响应消息时间',
      responseCount: '响应发送/接收次数',
      responseLastTime: '响应最后时间',
      responseMessageDigest: '响应消息摘要',
      responseSignalID: '响应回执编号',
      responseSignalDate: '响应回执时间',
      owner_id: '自己内部编号',
      partner_id: '合作伙伴内部编号',
      pip_id: 'PIP定义内部编号',
      timeStart: '开始时间',
      timeEnd: '结束时间'
    },
    dialog: {
      title: 'Rosettanet详情',
      tabs: {
        basic: '基础',
        log: '日志',
        attachment: '附件'
      },
      log: {
        payload: '报文',
        logTime: '时间',
        logType: '操作',
        logContent: '内容'
      },
      requestAttachments: '请求附件',
      responseAttachments: '响应附件',
      attach: {
        payload: '附件',
        contentDescription: '描述'
      }
    }
  },
  reportProcessSingle: {
    stepName: '流程步骤',
    stepTime: '耗时(毫秒)'
  },
  configFolder: {
    name: '配置目录',
    props: '附加属性',
    props_description: '可以使用键label.en设置其他语言显示名称'
  },
  accountManagement: {
    name: '账号管理',
    basic: '基本',
    lockFlag: '锁定',
    passwordExpireDate: '密钥过期时间',
    dataAuth: {
      name: '数据权限',
      auditCriteria: '业务数据过滤',
      auditCriteria_description: 'JSON格式的针对sysAudit的过滤条件',
      configFolderCriteria: '配置目录过滤',
      configFolderCriteria_description: '没有选择能够访问所有的配置目录',
      dmProjectCriteria: 'API项目过滤',
      dmProjectCriteria_description: '没有选择能够访问所有的API项目'
    },
    funcAuth: { name: '功能权限' }
  }
}
