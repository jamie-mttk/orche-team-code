export default {
  _: {
    key: 'Key',
    id: 'ID',
    value: 'Value',
    name: 'Name',
    description: 'Description',
    type: 'Type',
    operation: 'Operation',
    ok: 'OK',
    cancel: 'Cancel',
    close: 'Close',
    add: 'Add',
    del: 'Delete',
    edit: 'Edit',
    save: 'Save',
    copy: 'Copy',
    up: 'Up',
    down: 'Down',
    view: 'View',
    import: 'Import',
    export: 'Export',
    upload: 'Upload',
    download: 'Download',
    exportExcel: 'Export Excel',
    search: 'Search',
    choose: 'Choose',
    reset: 'Reset',
    refresh: 'Refresh',
    clear: 'Clear',
    start: 'Start',
    stop: 'Stop',
    success: 'success',
    fail: 'fail',
    parent: 'Parent',
    all: 'All',
    active: 'Active',
    deactive: 'Disable',
    batchActive: 'Batch active',
    batchDeactive: 'Batch disable',
    tableNoData: 'No data, please click button Add',
    tableNoData2: 'No data',
    confirm: 'Confirm',
    confirmDel: 'Do you confirm to delete?',
    delSuccess: 'Delete successfully',
    delFail: 'Delete failed',
    confirmSave: 'Do you confirm to save?',
    saveSuccess: 'Saved successfully',
    saveFail: 'Save failed',
    upload_exceed: 'Only allow to upload one file every time',
    upload_no_file: 'Please choose the file(s) to upload',
    uploadSuccess: 'Upload successfully',
    uploadFail: 'Upload failed',
    downloadFail: 'Download failed',
    copySuccess: 'Copy successfully',
    encoding: 'Encoding',
    more: 'More',
    insertTime: 'Create time',
    updateTime: 'Update time',
    expertMode: 'Expert mode',
    yes: 'Yes',
    no: 'No',
    pleaseChoose: 'Please choose',
    choosed: 'Choosed',
    unchoosed: 'Unchoosed',
    lang: {
      notify:
        'Languange changed to {0} successfully,you may need to refresh to make it take affect!'
    }
  },
  __: {
    entry: 'Entry',
    process: 'Process',
    reentry: 'Reentry',
    pool: 'Pool',
    resource: 'Resource',
    mapper: 'Web Mapper',
    scheduler: 'Scheduler',
    retryStrategy: 'Retry strategy',
    variable: 'Vaiable',
    sequence: 'Sequence',
    runtimeStrategy: 'Execute strategy',
    channel: 'Channel',
    cache: 'Cache',
    lookup: 'Lookup',
    authentication: 'Authentication',
    authorization: 'Authorization',
    solutionDm: 'API solution',
    solutionRms: 'Receive Mapping Send solution',
    status: 'Status',
    docType: 'Doc type',
    domain: 'Domain',
    subject: 'Subject',
    sender: 'Sender',
    receiver: 'Receiver',
    transaction: 'Transaction id',
    payloadViewer: 'Payload viewer',
    plsChooseRows: 'Please choose the rows to operate first',
    sureToOperate: 'Are you sure to continue',
    doneWithResult: 'Operate finish with result:',
    dataSize: 'Size',
    dataCount: 'Count'
  },
  layout: {
    setting: {
      title: 'Layout screen setting',
      themeChoose: 'Theme choose',
      themeSample: 'Theme samples'
    }
  },
  _authorization: {
    AuthenticationChooser: {
      principal: 'Principal(User name)'
    }
  },
  _Cert: {
    certType: 'Cert category',
    CertAlias: {
      certList: 'Certificate list',
      alias: 'Alias',
      keyStore: 'Key store',
      certificate: 'Certificate',
      privateKey: 'Private key',
      createCert: 'Create certificate',
      expireDays: 'Expire days',
      dn_CN: 'Common name',
      dn_OU: 'Organization Unit',
      dn_O: 'Organization',
      dn_L: 'Location',
      dn_S: 'State',
      dn_C: 'Country',
      signAlgorithm: 'Sign algorithm',
      keySize: 'Key size',
      viewCert: 'View certificate'
    },
    CertKeyStore: {
      keyStoreList: 'Key store list',
      aliasCount: 'Alias count',
      keyStoreEdit: 'Key store Edit'
    },
    CertPicker: {
      chooseCert: 'Choose certificate'
    },
    ExportCertDialog: {
      title: 'Export certificate',
      format: 'Format',
      format_binary: 'Binary(DER)'
    },
    ExportKeyStoreDialog: {
      title: 'Export key store',
      format: 'Format',
      password: 'Password',
      password2: 'Confirmed password',
      error_no_password: 'No password input',
      error_password_unmatch: 'Password and confirmed password is not matched'
    },
    ExportPrivateKeyDialog: {
      title: 'Export key store',
      format: 'Format',
      format_binary: 'Binary(DER)',
      password: 'Password',
      password2: 'Confirmed password',
      error_no_password: 'No password input',
      error_password_unmatch: 'Password and confirmed password is not matched'
    },
    ImportCertDialog: {
      title: 'Import certificate',
      alias: 'Alias',
      format: 'Format',
      format_binary: 'Binary(DER)',
      file: 'File',
      file_choose: 'Choose file',
      file_choose_tip: 'Choose the file to upload and then click OK button',
      error_no_alias: 'Alias is not input',
      error_no_cert: 'Certificate is not input',
      error_only_one_file: 'Only one file is allowed to upload'
    },
    ImportKeyStoreDialog: {
      title: 'Import key store',
      format: 'Format',
      password: 'Password',
      file: 'File',
      file_choose: 'Choose file',
      file_choose_tip: 'Choose the file to upload and then click OK button',
      error_no_password: 'Password is not input',
      error_no_privateKey: 'Private key is not input',
      error_only_one_file: 'Only one file is allowed to upload'
    },
    ImportPrivateKeyDialog: {
      title: 'Import private key',
      alias: 'Alias',
      format: 'Format',
      format_binary: 'Binary(DER)',
      privateKey_file: 'Private key file',
      privateKey_file_choose: 'Choose private key file',
      password: 'Password',
      certificate_file: 'Certificate file',
      certificate_file_choose: 'Choose certificate file',
      error_no_alias: 'Alias is not input',
      error_no_privateKey: 'Private key is not input',
      error_no_certificate: 'Certificate is not input',
      error_only_one_file: 'Only one file is allowed to upload'
    }
  },
  _ConfigDyna: {
    Chooser: {
      unsuportedType: 'Unsuported choose type',
      error_get_list: 'Get list error',
      choose_to_edit: 'Please choose the data to edit from list',
      choose_to_del: 'Please choose the data to delete from list'
    },
    dataChooser: {
      retryConfig: {
        name: 'Retry strategy',
        max: 'Max',
        interval: 'Interval(Second)',
        intervalInc: 'Interval add(Second)'
      },
      schedulerConfig: {
        name: 'Scheduler',
        startTime: 'Start time',
        endTime: 'End time',
        mode: 'Mode',
        mode_options:
          'SIMPLE:Simple,CRON:CRON,DAILY:Daily interval,CALENDAR:Calendar interval,COMBINATION:Combination',
        repeatInterval: 'Interval',
        repeatUnit: 'Unit',
        repeatUnit_options: 'hour:Hour,minute:Minute,second:Second,milliSecond:Millisecond',
        repeatCount: 'Repeat count',
        repeatCount_description:
          '-1 represents infinite repetition; \r\n WHile set to other value, the actual repeat count is the set value plus 1',
        misFire: 'Miss fire strategy',
        misFire_description:
          'The processing strategy for handling missed triggers due to certain reasons, such as service shutdown \r\n The threshold for missing the definition is defined in the file conf/quartz.properties using the parameter org.quartz.jobStore.misfireThreshold',
        misFire_simple_smart:
          'MISFIRE_INSTRUCTION_SMART_POLICY. repeat count=0,MISFIRE_INSTRUCTION_FIRE_NOW; -1,MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT; other,MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT',
        misFire_simple_ignore: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. Trigger all missed',
        misFire_simple_1:
          'MISFIRE_INSTRUCTION_FIRE_NOW. repeat count!=0, MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;Other,trigger immediately',
        misFire_simple_2:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT. Trigger immediately,repeat count is not changed.',
        misFire_simple_3:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT.Trigger immediately,deduct missing times from  repeat count.',
        misFire_simple_5:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT. Trigger at next trigger time,repeat count is not changed.',
        misFire_simple_4:
          'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT. Trigger at next trigger time,deduct missing times from  repeat count.',
        misFire_other_smart:
          'MISFIRE_INSTRUCTION_SMART_POLICY. Same as  MISFIRE_INSTRUCTION_FIRE_NOW',
        misFire_other_ignore: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. Trigger all missed',
        misFire_other_1:
          'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. Trigger immediately and then trigger according to CRON',
        misFire_other_2:
          'MISFIRE_INSTRUCTION_DO_NOTHING.Ignore missed and trigger at next time defined by CRON',
        cronExpression: 'CRON',
        dailyRepeatInterval: 'Interval',
        dailyRepeatUnit: 'Unit',
        dailyRepeatUnit_options: 'hour,minute,second',
        dailyStartTime: 'Daily start time',
        dailyEndTime: 'Daily end time',
        dailyDaysOfWeek: 'Days of weel',
        dailyDaysOfWeek_options:
          '1:Sundary,2:Monday,3:Tuesday,4:Wednesday,5:Thursday,6:Friday,7:Saturday',
        calendarRepeatInterval: 'Interval',
        calendarRepeatUnit: 'Unit',
        calendarRepeatUnit_options: 'year,month,week,day,hour,minute,second',
        combinationTrigger_mode: 'Mode',
        combinationTrigger_mode_options:
          'SIMPLE:Simple,CRON:CRON,DAILY:Daily interval,CALENDAR:Calendar interval'
      }
    },
    DateTimePicker: {
      startTime: 'Start time',
      endTime: 'End time',
      to: 'to',
      startDate: 'Start date',
      endDate: 'End date'
    },
    support: {
      fieldMandatory: 'Field is mandatory'
    },
    Select: {
      choosed: 'Choosed',
      unchoosed: 'Unchoosed',
      error_get_list: 'Get list error'
    },
    Table: {
      inputConfirm: 'Please input the name after copied',
      copied: "'s copy",
      copiedThis: "Current line 's copy",
      mandatoryInput: 'Field mandatory'
    }
  },
  _Page: {
    PageStandard: {
      active_no_url: 'No activeURL is configured',
      active_confirm: 'Are you confirm to active?',
      active_success: 'Active succesfully',
      active_fail: 'Active failed',
      deactive_no_url: 'No deactiveURL is configured',
      deactive_confirm: 'Are you confirm to disable?',
      deactive_success: 'Disable successfully',
      deactive_fail: 'Disable failed',
      error_get_data: 'Fail to get data',
      error_get_config: 'Fail to get config'
    },
    UiDialog: {
      basic: 'Basic',
      para: 'Parameter',
      uiNone_label: 'Remark',
      uiNone_value: 'This module does not need configuration',
      copyDone: 'Data ID copied!'
    },
    WizardDialog: {
      preStep: 'Previous',
      nextStep: 'Next',
      finish: 'Finish',
      close_confirm: 'Do you confirm to close this wizard？Unsaved data will be lost.'
    }
  },
  _utils: {
    datetimePicker: {
      today: 'Today',
      yesterday: 'Yesterday',
      weekAgo: 'One week ago',
      firstThisMonth: 'First of this month',
      firstLastMonth: 'First of last month'
    },
    request: {
      logout_confirm:
        'Please are logout, you can click cancel to keep in this screen, or login agian',
      login_again: 'Login again',
      unauth_text: 'Sorry, you are not authorized to access, please contact system administrator',
      unauth: 'Unauthorized',
      error500: {
        title: '500 Error',
        cause: 'Type',
        error: 'Error',
        detail: 'Detail'
      }
    }
  },
  _Plugin: {
    define: 'Definition',
    data: 'Data',
    noData: 'No data',
    error_no_edit: 'This plugin does not allow to edit data'
  },
  _solution: {
    Related: {
      cache: 'Cache',
      authentication: 'Authentication',
      authorization: 'Authorization',
      pool: 'Pool',
      entry: 'Entry',
      resource: 'Resource',
      process: 'Process',
      related: 'Related objects'
    }
  },
  _scriptBuilder: {
    data: {
      tips:
        'Script will be evaluated by system automatically to provide dynamic parameters.<br>' +
        'The following types are supported.Please note: mixed usage is not allowed.<br>' +
        'No prefix is needed to add during script editing.<br>' +
        '<b>Not script</b> Not start with any prefix below,not evaluation will be done.<br>' +
        '<b>Pipeline</b> Start with p: ,use to get value from pipeline.<br> Under HTTP call,http_para_xyz is the value of HTTP parameter xyz<br' +
        '<b>Variable</b> Start with v: , use to get value of global variable.<br>' +
        '<b>mvel script</b> Start with m: ,Mvel2.x script,is suit for complex caluation scenarios.<br>' +
        '<b>Jodd template</b> Start with j: , is suitable for adding a small number of variables in a large amount of text, such as mail templates.' +
        '<b>Pipeline</b>  includes the below often used keys：_subject(Subject),_sender(Sender),_receiver(Receiver),_transaction(Transaction, for example AS2 message id),_doc_type(Document type),' +
        '_result( The return value of previous process node),_payload(Default payload),_source_type(Process souce, includes ENTRY、ASYNC、HTTP_PROCESS/HTTP_PRE/HTTP_POST),' +
        '_source_id(Souce id, the meaning is different for differnet source type),_source_name(Source name,the meaning is different for differnet source type),_time_entry(The time the data arrive at entry,格式为 yyyy-MM-dd HH:mm:ss)<br>' +
        'And in API invoked process, key http_para_xyz can get the value of HTTP parameter xyz,http_header_efg can get the value of HTTP header efg<br>' +
        '<b>Event(event)</b>  includes the below often used keys:type(Event type,service:Service,process:Process,sys:System),source(Souce),sourceSub(Sub-source),content(Error content),exception(Exception object);' +
        ' when type=service:infos(infoMap),payloads(payload list);when type=process,data(log data),pipeline(pipeline data),node(current process node)',
      utilMethods: {
        now1: 'Get current date time,format yyyyMMddHHmmss',
        now2: 'Get current date time,format is provided by parameter',
        now2_pattern: 'Format',
        yesterday1: 'Get date time of yesterday,format yyyyMMddHHmmss',
        yesterday2: 'Get date time of yesterday,format is provided by parameter',
        yesterday2_pattern: 'Format',
        uuid: 'Get UUID,32 bytes, global unique',
        sequence: 'Get sequence value',
        sequence_key: 'Sequence key',
        sequence_delta: 'Delta',
        notEmpty: 'Value is not empty',
        notEmpty_val: 'Value',
        isEmpty: 'Value is empty',
        isEmpty_val: 'Value',
        serverSetting: 'Get server setting value',
        serverSetting_key: 'Setting key',
        serverSettings: 'Get server settings Map<String,Object>',
        sysProperty: 'Get enviroment property',
        sysProperty_key: 'Key',
        evalVariable: 'Evaluate variable value',
        evalVariable_key: 'Vairable key',
        loadPayload:
          'Load the value of the payload(Can be get by code p.get("_payload")) and convert to string by encoding',
        evalVariable_payload: 'Payload',
        evalVariable_encoding: 'Encoding'
      },
      pipelineMethods: {
        get: 'Get value from pipeline by key',
        get_key: 'Key',
        getString1: 'Get string from pipeline by key, retur null if not found',
        getString1_key: 'Key',
        getString2: 'Get string from pipeline by key, retur default value if not found',
        getString2_key: 'Key',
        getString2_defValue: 'Default value',
        getStringMandatory: 'Get string from pipeline by key,exception if nout found',
        getStringMandatory_key: 'Key',
        getInteger1: 'Get integer from pipeline by key, return null if not found',
        getInteger1_key: 'Key',
        getInteger2: 'Get integer from pipeline by key, return default value if not found',
        getInteger2_key: 'Key',
        getInteger2_defValue: 'Default value',
        getBoolean1: 'Get boolean from pipeline by key, return null if not found',
        getBoolean1_key: 'Key',
        getBoolean2: 'Get boolean from pipeline by key, return default value if not found',
        getBoolean2_key: 'Key',
        getBoolean2_defValue: 'Default value',
        containsKey: 'Determine whether the given key is existed in pipeline',
        containsKey_key: 'Key'
      },
      mevelConfig: {
        u: 'Util library - Can be used anywhere',
        p: 'Pipeline - Used in process',
        event: 'Event - Used in alert'
      },
      commonConditions: {
        lastError: 'Last node exceute failed(Error handling mode is ignore)',
        lastSuccess: 'Last node execute successfully',
        lastTrue: 'Last node return true',
        lastFalse: 'Last node return false',
        stringEqual: 'String value in pipelien equals(Replace xyz and abc)',
        stringNotEqual: 'String value in pipelien NOT equals(Replace xyz and abc)',
        integerEqual: 'Integer value in pipelien equals(Replace 123 and abc)',
        integerNotEqual: 'Integer value in pipelien NOT equals(Replace 123 and abc)'
      },
      processError: {
        label: 'Add process runtime error',
        options: {
          _errorProcess: 'Process name',
          _errorProcessId: 'Process ID',
          _errorNode: 'Error node',
          _errorTime: 'Error time',
          _errorInfo: 'Error info',
          _errorDetail: 'Error detail',
          _errorDetailSingle: 'Error detail(Single line)',
          _errorDetailCause: 'Error cause',
          _errorHappen: 'Is error happened(Boolean)'
        }
      }
    },
    index: {
      title: 'Script editor',
      NoneScript: 'None script',
      PipelineScript: 'Pipeline value',
      VariableScript: 'Variable',
      MvelScript: 'Mvel script',
      JoddScript: 'Jodd template',
      test: 'Test'
    },
    JoddMethodBuilder: {
      title: 'Add method code',
      pipeline: 'Pipeline',
      event: 'Event',
      pipelineKey: 'Pipeline key',
      eventKey: 'Event key'
    },
    JoddScript: {
      addMethod: 'Add method code',
      scriptArea_placeholder: 'Please input script'
    },
    MvelMethodBuilder: {
      title: 'Add method code'
    },
    MvelMethodBuilderDetail: {
      title: 'Method parameter setting',
      noPara: 'This method does not need parameter,please click to confirm insert'
    },
    MvelScript: {
      addMethod: 'Add method code',
      addString: 'Add string',
      scriptArea_placeholder: 'Please input script',
      addString_prompt: 'Please input string to add',
      commonCondition: 'Common process connection condition'
    },
    PipelineScript: {
      applyValue_placeholder: 'Please input pipeline key'
    },
    ScriptTest: {
      title: 'Script test',
      test: 'Test',
      testFinished: 'Test finished'
    },
    ScriptTestData: {
      pipelineEnable: 'Simulate pipeline data',
      pipeline: 'Pipeline data',
      eventEnable: 'Simulate event data',
      event: 'Event data',
      script: 'Script',
      resultType: 'Result type',
      resultContent: 'Result'
    },
    VariableScript: {
      applyValue_placeholder: 'Please choose variable'
    }
  },
  _util: {
    PayloadViewerTable: {
      viewPaload: 'Payload view',
      error_no_payload: 'No payload to download'
    },
    ShowPayloadDialog: {
      replaceToLF: 'Replace to line feed',
      autoWrapLine: 'Auto wrap line',
      viewPaload: 'View payload',
      replacer_1: 'Not replaced',
      replacer_2: '(Single quotation)',
      replacer_3: '`(Back quote)',
      replacer_4: '+(Plus)',
      beautifyOutput: 'Format data',
      payloadSize: 'Payload size'
    }
  },
  navbar: {
    welcome: 'Welecome to Cloud Hub 5 Administrator Console',
    dashboard: 'Dashboard',
    profile: 'User profile',
    logout: 'Logout'
  },
  tagsView: {
    refresh: 'Refresh',
    close: 'Close',
    closeOthers: 'Close Others',
    closeAll: 'Close All'
  },
  login: {
    title: 'Login Form',
    login: 'Login',
    username: 'User name',
    password: 'Password',
    error_no_username: 'Please input user name',
    error_no_password: 'Please input password',
    error_login_fail: 'User name or password error, please login again',
    change_password: 'Change password',
    error: {
      UnknownAccount: 'Unkown account',
      LockedAccount: 'Account locked',
      CredentialExipred: 'Password expired,please change password',
      ExcessiveAttempts: 'Exessive attempts, account locked',
      IncorrectCredentials1: 'Password error.<br>Time range:',
      IncorrectCredentials2: ' seconds,already retried ',
      IncorrectCredentials3: ' times, max allow retry',
      IncorrectCredentials4: 'infinite',
      IncorrectCredentials5:
        ' times.<br>Please note account will be locked if max allowed retry is exceed'
    }
  },
  dashboard: {
    auditToday: 'Traffic today',
    auditTodaySuccess: 'Success today',
    auditTodayFail: 'Fail today',
    eventToday: 'Event today',
    auditByStatus: 'Traffic today(By status)',
    auditByType: 'Traffic today(By type)',
    auditByDomain: 'Traffic today(By Domain)',
    auditByServer: 'Traffic today(By server)',
    event_title: 'Last event information',
    event_all: 'Show all',
    auditDailyConfig_title: 'Daily traffic',
    auditDailyConfig_auditToday: 'Today',
    auditDailyConfig_auditYesterday: 'Yesterday',
    auditLast14Config_title: 'Last 14 days traffic',
    auditTop10Transaction: 'Traffic today TOP 10',
    auditTop10TimeCost: 'Time cost today TOP 10(ms)'
  },
  audit: {
    infoName: 'Info',
    CheckpointDialog_title: 'View checkpoint',
    ColManDialog_title: 'Manage search result display columns',
    ColManDialog_choosed: 'Choosed columns',
    ColManDialog_unchoosed: 'Unchoosed columns',
    timeCost: 'Process cost(ms)',
    sourceBefore: 'Entry cost(ms)',
    payloadSize: 'Payload size',
    processInstanceId: 'Process instance id',
    process: 'Process',
    sourceType: 'Source type',
    sourceName: 'Source',
    errorInfo: 'Error info',
    errorNode: 'Error node',
    errorTime: 'Error time',
    retryTime: 'Retry time',
    channel: 'Channel',
    strategy: 'Strategy',
    server: 'Execute server',
    sourceServer: 'Entry server',
    createTime: 'Create time',
    lastTime: 'Update time',
    sourceTime: 'Receive time',
    status_fail: 'Fail',
    status_retry: 'Retrying',
    status_run: 'Running',
    status_abort: 'Abort',
    status_wait: 'Wait',
    status_success: 'Success',
    sourceType_entry: 'Entry',
    sourceType_async: 'Async',
    sourceType_process: 'HTTP process entry',
    sourceType_pre: 'HTTP preprocess',
    sourceType_post: 'HTTP postprocess',
    sourceType_fork: 'FORK',
    preHandler: '(Special)Pre handler',
    postHandler: '(Special)Post handler',
    _api_audit_id: 'API info',
    criteria: {
      tip: 'String fields can use vertical lineto split multiple query conditions, and asterisk to match multiple characters. This feature is also supported by other queries in this software.',
      hist: 'Database',
      sortBy: 'Sort by',
      sortType: 'Sort type',
      sortType_asc: 'Ascending',
      sortType_desc: 'Descending',
      savedCriteria: 'Saved criteria',
      savedCriteria_button: 'Save criteria',
      customize_field_button: 'Customize field name',
      createTimeStart: 'Time from',
      createTimeEnd: 'Time to',
      pickerOptions_today: 'Today',
      pickerOptions_yesterday: 'yesterday',
      pickerOptions_8pm_yesterday: '8pm yesterday',
      pickerOptions_one_week: 'One week ago',
      pickerOptions_begin_this_month: 'Beinning of this month',
      infoCustomizeConfig_title: 'Info filed name setting',
      delCriteria_confirm: 'Do you want to delete saved criteria',
      retryFlag: 'Retry flag',
      retryFlag_all: 'All',
      retryFlag_yes: 'Have retry',
      retryFlag_no: 'No retry',
      timeCostFrom: 'Time cost from(ms)',
      timeCostTo: 'Time cost to(ms)'
    },
    detail: {
      info: 'Basic',
      previous: 'Previous',
      next: 'Next',
      downloadPayload: 'Download payload',
      viewPayload: 'View payload',
      checkpoint: 'Checkpoint',
      checkpoint_start: 'Start',
      checkpoint_process: 'Process',
      checkpoint_user: 'User',
      customize: 'Customized fields and payloads',
      log: 'Log',
      detail: 'Detail',
      startTime: 'Start time',
      content: 'Content',
      relatedValue: 'Related value',
      extra: 'Extra',
      timeCost: 'Time cost(ms)',
      pis: 'Processes lauched',
      log_entry: 'Receive data',
      log_entry_from: 'from',
      log_abort: 'Process abort',
      log_abort_sys: 'System abort(Restart to resume)',
      abort_prehandler: 'Prehandle of preprocess return false,abort',
      log_start: 'Process start',
      log_success: 'Process success',
      log_retry: 'Process retry',
      log_fail: 'Process fail',
      log_forceExit: 'Process exit',
      log_unexpected: 'Unexpected exception',
      log_preHandler: 'Process pre-handler',
      log_postHandler: 'Process post-handler',
      extra_retry: 'Retry',
      extra_ignore: 'Ignore exception',
      extra_forceExit: 'Exit',
      extra_isSkip: 'Skip',
      extra_reentry_wait: 'Reentry wait',
      extra_reentry_EVENT: 'Reentry event',
      extra_reentry_TIMEOUT: 'Reentry timeout',
      extra_join_wait: 'Join wait',
      extra_join_continue: 'Join continue',
      extra_desc_retry:
        'Exception occur and automatically retry according to the retry strategy setting',
      extra_desc_ignore: 'Exception occur and ignored according to node configuration',
      extra_desc_forceExit: 'The node action request to exit',
      extra_desc_isSkip: 'This step is deactived and skipped',
      extra_desc_reentry_wait: 'This step is reentry, wait for external event or timeout',
      extra_desc_reentry_EVENT: 'This step is reentry,received external event and continue',
      extra_desc_reentry_TIMEOUT: 'This step is reentry,timeout and continue',
      extra_desc_join_wait: 'This step is join, wait because not all forked processes are finished',
      extra_desc_join_continue: 'This step is join, all forked processes are finished and continue',
      log_return: 'return',
      error_detail: 'Error detail',
      error_no_payload: 'No payload to download'
    },
    index: {
      criteria: 'Criteria',
      result: 'Result',
      detail: 'Detail'
    },
    result: {
      fetchData: 'Refresh',
      fetchData_description: 'Refresh according to current criteria',
      restart: 'Restart',
      restart_description: 'Restart from process beginning, start checkpoint is needed',
      resume: 'Resume',
      resume_description: 'Resume from error code, process checkpoint is needed',
      checkpoint: 'Checkpoint',
      checkpoint_description: 'Resume from user checkpoint',
      stop: 'Stop',
      stop_description: 'Stop running processes',
      stopCluster: 'Stop in cluster',
      stopCluster_description:
        'Under cluster the server this management console connected may be different with the server the process is running. This button try to get the process running server and send abort request.The target server info is get from the first choosed data.',
      stopCluster_error: 'Fail to get target server,request can not be sent',
      downloadPayload: 'Download payload',
      downloadPayload_description: 'Download the default payload',
      exportExcel: 'Export Excel',
      exportExcel_description: 'Export the below table to Excel',
      colSettingMan: 'Column setting',
      colSettingMan_description: 'Set the fields shown in below table',
      resetColSetting: 'Column reset',
      resetColSetting_description: 'Reset the displya columns to default setting',
      tagAndComment: 'User tag and comment',
      tagAndComment_description:
        'For example used to indicate the handling of this record, or use as your wish',
      tag: 'User tag',
      comment: 'User comment',
      tag_none: 'No user tag',
      tagAndComment_set_success: 'Set user tag and comment succesfully',
      error_get_data: 'Get data failed',
      operate_prompt1: 'Please choose',
      operate_prompt2: "'s row",
      msg_text1: 'success,totally',
      msg_text2: 'rows,success',
      msg_text3: 'rows',
      msg_text4: 'Allow',
      msg_text5: 'Not allowed',
      msg_success: 'success',
      msg_fail: 'fail',
      error_not_select: 'Please choose the rows first',
      confirm_reset: 'Do you confirm to reset columns setting?'
    },
    SaveCriteriaDialog: {
      title: 'Save criteria',
      overwrite: 'Overwrite',
      name_placeholder: 'Please input name',
      error_no_name: 'No name is input',
      error_no_criteria: 'Please choose the overwrite criteria'
    }
  },
  alert: {
    insertTime: 'Log time',
    type: 'Type',
    source: 'Source',
    sourceSub: 'Detail source',
    content: 'Content',
    exceptionHandler: 'Exception handler',
    AlertDialog: {
      title: 'View alert detail',
      basic: 'Basic',
      exception: 'Exception',
      infos: 'Info',
      payloads: 'Payload',
      data: 'Process data',
      pipeline: 'Process pipeline'
    },
    data: {
      hist: 'Database',
      type_options: 'service:Service,process:Process,system:System',
      timeStart: 'Time start',
      timeEnd: 'Time end',
      exportExcel: 'Export Excel'
    }
  },
  cluster: {
    index: {
      setting: 'Setting',
      performance: 'Performance',
      memory: 'Memory',
      threads: 'Thread',
      log: 'Log',
      service: 'Service',
      dispatch: 'Dispatch',
      license: 'License',
      msg_choosed: 'The data displayed in tab belongs to server',
      msg_clean_mem1: 'Garbage collection successfully.<br>Before clean:',
      msg_clean_mem2: 'M<br>After clean:',
      title_setting: 'Server information'
    },
    data: {
      gc: 'GC',
      showInfo: 'Sever info',
      tableTitle: 'Server list',
      server: 'Instance',
      name: 'Server name',
      port: 'Port',
      insertTime: 'Cluster join time',
      lastTime: 'Last heart beat time'
    },
    performance: {
      cpuUsageConfig: {
        user: 'User',
        sys: 'System'
      },
      memUsageConfig: {
        title: 'Memory(M)',
        globalTotal: 'Max physical',
        globalUsed: 'Used physical',
        vmTotal: 'Max VM',
        vmUsed: 'Used VM'
      }
    },
    memory: {
      categoryShow: 'Showed category',
      memory_usage: 'Current usage',
      memory_peakUsage: 'Peak usage',
      unit: 'Unit',
      unit_b: 'Byte',
      unit_k: 'Kilo byte',
      unit_m: 'Mega byte',
      title_heapAndNoneHeap: 'Head and None-Heap',
      data: {
        name: 'Name',
        init: 'Init',
        used: 'Used',
        committed: 'Committed',
        max: 'Max',
        usedPercentage: 'Used percentage(%)',
        manager: 'Manager'
      },
      title_directPool: 'Direct memory',
      data1: {
        name: 'Name',
        count: 'Count',
        memoryUsed: 'Memory used',
        totalCapacity: 'Total capacity'
      },
      title_collectors: 'Garbage colelctors',
      data2: {
        name: 'Name',
        count: 'Count',
        time: 'Time(ms)',
        memoryPools: 'Memory pools'
      }
    },
    thread: {
      checkLocked: 'Check thread lock',
      checkLockedTitle: 'Check thread lock result',
      checkLockedNone: 'No thread is locked',
      lineNumber: 'Line no#',
      methodName: 'Method',
      className: 'Class',
      empty: 'Empty',
      threadName: 'Thread name',
      threadGroup: 'Thread group',
      priority: 'Priority',
      state: 'State',
      group: 'Thread group',
      trace: 'Breakpoint',
      dumpHeapButton: 'Dump Heap',
      dumpHeapPrompt:
        'Are you to dump heap and download? This may be slow, please find the proper file under cloud hub tmp folder if time out.The download file can be analyzed by tools,such as Eclipse Memory Analyzer!'
    },
    log: {
      dialog_title: 'Log data',
      size: 'Size',
      lastModified: 'Last modified'
    },
    service: {
      confirm_prompt: 'Do you confirm to ',
      config: 'Config',
      status: 'Status',
      category: 'Category',
      implClass: 'Implemention class',
      depends: 'Depend',
      autoStart: 'Auto start',
      category_options: 'SYS:System,USER:User'
    },
    dispatch: {
      manage: 'Manage',
      priority: 'Priority',
      concurrent: 'Concurrent',
      running: 'Running',
      waiting: 'Waiting',
      removeSel: 'Remove selected',
      removeAll: 'Remove all',
      moveSel: 'Move selected',
      moveAll: 'Move all',
      data: {
        time_entry: 'Time',
        subject: 'Subject',
        source_server: 'Source server',
        source_type: 'Source type',
        source_name: 'Source name',
        content: 'Content'
      },
      waitingMsg: 'Waiting messages',
      move_prompt: 'Please select the target channel',
      targetChannel: 'Target channel',
      operate_text1: 'Do you confirm to ',
      operate_text2: ' move ',
      operate_text3: ' remove ',
      operate_text4: ' all ',
      operate_text5: ' selected ',
      operate_text6:
        ' messages?<br>Please note,the operate can NOT undo and may lost data.<br>Please contact system administrator if it is not clear!!',
      error_no_data: 'Please select the rows to operate',
      success_text1: 'process succefully,totally ',
      success_text2: ' rows',
      resetChannel_confirm:
        'Are you sure to reset the running count of all requests for the channel? After resetting , the channel can handle the maximum number of requests allowed',
      resetChannel_result: 'Reset successfully'
    },
    license: {
      request: 'Request',
      request_description: 'Click reqeust button and send saved file to license generator',
      title: 'License reqeust',
      customer: 'Customer',
      remark: 'Remark',
      period: 'Expire period(days)',
      msg_success: 'License sucessfully,exipre date',
      msg_fail: 'License failed,reason'
    }
  },
  various: {
    index: {
      runningScheduler: 'Running scheduler',
      activeScheduler: 'Active scheduler',
      activePool: 'Pool',
      entityPara: 'Runtime parameter'
    },
    runningScheduler: {
      description: ' List running schedulers in all servers.',
      del_prompt:
        'Remove may cause one scheduler to run in more than one servers at same time.It is not recommanded to remove if you do not know what you are doing.Continue?',
      keyGroup: 'Group',
      keyDescription: 'Name',
      instanceId: 'Server',
      time: 'Start time'
    },
    activeScheduler: {
      state: 'Status',
      keyGroup: 'Group',
      description: 'Name',
      startTime: 'Start time',
      endTime: 'End time',
      previousFireTime: 'Previous fire time',
      nextFireTime: 'Next fire time'
    },
    activePool: {
      trial: 'Trial',
      trial_prompt:
        'Trial will try to create pool and release it immediately to test whether config is correct,Continue?',
      trial_text1: 'Pool create successfully',
      trial_text2: 'Pool implemenatation does not support trial',
      trial_success: 'Trial successfully',
      trial_fail: 'Trial failed',
      monitor_settingMax: 'Max set',
      monitor_settingIdle: 'Idle set',
      monitor_actualMax: 'Max actual ',
      monitor_actualIdle: 'Idle actual'
    },
    entityPara: {
      error_type: 'Unsuported data type,it is not allowed to modify'
    }
  },
  entry: {
    trial: 'Trial',
    trial_confirm:
      'Do you want to trial? Please note under cluster enviroment, the scheduler may also trigger the job ,so the potential conflict may happen.',
    meta: 'Metadata',
    meta_description:
      'Metadata will be add into pipeline\n Process could read these key-value settings.'
  },
  process: {
    ActionDialog: {
      exceptionHandler: 'Exception handler',
      exceptionHandler_options:
        'escalate:Escalate,ignore_alert:Ignore with alert,ignore:Ignore without alert',
      logMode: 'Log mode',
      logMode_description:
        'Set the log target of action log.System log means all the action logs are written into system standard log;Independent means log independently which can be viewed in transaction screen',
      logMode_options: 'none:None,system:System log,solo:Independent',
      logLevel: 'Log level',
      logLevel_options: 'ERROR,WARN,INFO,DEBUG',
      sequence: 'Sequence',
      filename: 'Download filename',
      infoTabConfig: 'Info parameter',
      infoTabConfig_description:
        'They will be shown in transaction monitor user interface to simplify the search and monitor\n Normally they are used to show some important fields,such as order no#',
      payloadTabConfig: 'Payload parameter',
      payloadTabConfig_description:
        'They will be shown in transaciotn monitor user interface\nNormally they are used to show the translated payload'
    },
    Designer: {
      return_confirm: 'Process is changed,do you confirm to exit without saving?'
    },
    FieldSet: {
      infoTableConfig: 'Info fields',
      sequence: 'Sequence',
      format: 'Format',
      format_options: 'string:String,json:JSON,list:List'
    },
    Panel: {
      initConnConfig: {
        title: 'Connection setting',
        condition: 'Condition',
        priority: 'Priority'
      },
      detach_confirm: 'This operation will remove this connection,continue?'
    },
    Pallet: {
      filter_placeholder: 'Please input fitler'
    },
    ProcessNode: {
      startNode: 'Set as start node'
    },
    Toolbar: {
      returnList: 'Return list',
      processInfo: 'Process information'
    }
  },
  pool: {
    poolEnabled: 'Pooled'
  },
  mapping: {
    deploy: 'Upload and deploy',
    deploy_text1: 'Mapping list,totally ',
    deploy_text2: ' rows',
    deploy_text3: 'No mapping is deployed',
    deploy_text4: 'Exception list,totally ',
    deploy_text5: 'No exception',
    deploy_result: 'Deploy result',
    sourceMessageType: 'Type',
    sourceMessagePath: 'Source message',
    destMessageType: 'Type',
    destMessagePath: 'Destination message',
    show: {
      button: 'Show',
      messageSource: 'Source message',
      messageSourceConfig: 'Source message config',
      messageDest: 'Destination message',
      messageDestConfig: 'Destination message config',
      code: 'Code',
      info: 'Info',
      path: 'Path',
      event: 'Event'
    }
  },
  runtimeStrategy: {
    strategyRetry: 'Retry',
    strategyPersistent: 'Persistent',
    strategyLog: 'Log',
    strategyCheckpointStart: 'Checkpoint start',
    strategyCheckpoint: 'Checkpoint process',
    strategyExceptionDump: 'Exception DUMP',
    strategyDebugMode: 'Debug mode',
    informEvent: 'Alert while exception',
    strategyRetry_options: 'RETRY:Retry,IGNORE:Ignore',
    strategyPersistent_options:
      'END:End,START_END:Start and end,NONE:None,ALL:Every step,ON_ERROR:On error',
    strategyLog_options: 'ALL:All,NONE:None,SMART:Important',
    strategyCheckpointStart_options:
      'SAVE_DELETE:Save first and delete while done successfully,NO_SAVE:No save,ALWAYS_SAVE:Always save',
    strategyCheckpoint_options: 'SAVE_ERROR:On error,NO_SAVE:No save,ALWAYS_SAVE:Always save',
    strategyExceptionDump_options: 'SIMPLE:Simple,FULL:Full'
  },
  channel: {
    persistent: 'Persistent',
    priority: 'Priority',
    concurrent: 'Concurrent',
    defaultChannel: 'Default',
    submitMode: 'Submit node',
    submitMode_desc:
      'Under classic mode all async requets will be push into queue and waiting for dispatch; \nUnder smart mode the requests will be executed immediately if there are spare threads,otherwise reverse back to classic mode',
    submitMode_options: 'CLASSIC:Classic mode,SMART:Smart mode',
    bindingServers: 'Binding servers',
    bindingServers_desc:
      'The channel is adopt to all server if not set,\notherwise it is only adopt to the server setting here. \nThe value is the server instances seperated by comma.',
    mode: 'Mode',
    mode_desc: 'Default is local.Remote mode use mongoDB to consolidate',
    mode_options: 'LOCAL:Local,GLOBAL:Global'
  },
  variable: {
    expression: 'Expression'
  },
  cache: {
    data_unchoose: 'Please choose the cache to view'
  },
  lookup: {
    data_unchoose: 'Please choose the lookup table to edit',
    clear_confirm: 'Do you confirm to clear the data?',
    clear_text1: 'Totally cleared',
    clear_text2: 'rows'
  },
  authentication: {
    data_unchoose: 'Please choose first',
    principal: 'Principal(Username)',
    credential: 'Credential(Password)',
    dialog_title: 'Authentication data',
    dialog_basic: 'Basic',
    dialog_authorization: 'Authorization'
  },
  authorization: {
    data_unchoose: 'Please choose from list first',
    dialog_title: 'Authorization data',
    sequence: 'Sequence',
    public: 'Public access',
    resources: 'Resource',
    method: 'method',
    method_options: 'ALL:All,GET:GET,POST:POST',
    pattern: 'Regular expression',
    props: 'Additon properties'
  },
  solutionDm: {
    solution: 'Solution',
    auth: 'External system',
    auth_description: 'Choose the external systems this API can access',
    interceptor: 'Interceptor',
    related: 'Related',
    doc: 'Doc',
    project: 'Project',
    pleaseChoose: 'Please choose solution first',
    top: {
      importOas3: ' Import OAS3 file',
      apiList: 'API list',
      apiDevelop: 'API develop',
      tag: 'API tag',
      externalSystem: 'External system',
      schema: 'Schema',
      portalUser: 'Portal user',
      interceptor: 'Interceptor'
    },
    data: {
      title: 'API project',
      basic: 'Basic',
      authentication: 'Authentication',
      audit: 'Audit',
      portal: 'Portal',
      interceptor: 'Interceptor',
      name_description:
        'Used to identify this solution<br>And it is also the name of generated HTTP entry、authentication、authorization、cache and the process to get token',
      description_description:
        'It is also the description of generated HTTP entry、authentication、authorization、cache and the process to get token',
      entryHttpHost: 'Binding IP',
      entryHttpHost_description:
        'API will bind to this IP<br>Empty means binding to all IP<br>More complex setting,such as HTTPS can be set in generated HTTP entry',
      entryHttpPort: 'Binding port',
      urlExternal: 'Access URL',
      urlExternal_description:
        'The URL for external system to access. It is not set the access URL is combined with the binding IP and port',
      uriMatchMode: 'URI match mode',
      uriMatchMode_options: 'STARTWITH:Start with,EQUAL:Equal,SPRING:Spring MVC like',
      generatePathMode: 'Generate mode',
      generatePathMode_description:
        'The mode how to generate HTTP entry path\nLegacy mode generate path for each API\nAdvanced mode generate one service paht to handle all APIs',
      generatePathMode_options: 'legacy:Legacy,advanced:Advanced',
      approvalMode: 'Approval mode',
      approvalMode_description:
        'Defaultly approval is diabled\r\nIf enabled, API operations(add/edit/remove/disabled) should be approved before launch.',
      approvalMode_options: 'no:Disabled,all:Enabled',
      errorHandler: {
        label: 'Error handler',
        processErrorHandler: 'Error handler mode',
        processErrorHandler_options:
          'ERROR500:500 error,IGNORE:Ignore error and process provide response,CUSTOMIZE:Customize,PROCESS:Process',
        pehCustomizeCode: 'Customize HTTP code',
        pehCustomizeContentType: 'Customzie Content Type',
        _ie_pehCustomizeContent: 'HTTP response content',
        _ie_pehCustomizeContent_description:
          'This content will be evaluated after process is executed',
        pehProcess: 'Error handler process',
        pehRuntimeStrategy: 'Error handler runtime strategy'
      },
      auditable: 'Audit',
      auditable_description: 'Whether to audit API log',
      authType: 'Auth type',
      authType_options: 'none:Not authrorized,oauth2:Open Auth 2,basic:HTTP Basic',
      realm: 'Realm',
      tokenUri: 'URI to get token',
      tokenLength: 'Token length',
      tokenExpiresIn: 'Token expire in(Second)',
      tokenGenMode: 'Token generate mode',
      tokenGenMode_options: 'NONE:No restrict,REUSE:Reuse',
      tokenReuseThreshhold: 'Token reuse threadhhold(Second)',
      tokenReuseThreshhold_description:
        'If the difference between the expire time of saved token and now is greater then this value,then token will be reused. This value must be greater then 0.',
      cacheType: 'Cache type',
      cacheType_description:
        'Use to store token<br>Ehcache:Local storage,Redis:Remote storage<br>Redis should be used in cluster',
      cacheType_options: 'ehcache:Ehcache(Local),redis:Redis(Remote)',
      cacheEhcacheHeap: 'In heap memory',
      cacheEhcacheHeap_description: 'Number of objects stored in memory',
      cacheRedisConn: 'Redis connection',
      cacheExpireTti: 'Time to idle TTI(Second)',
      cacheExpireTti_description:
        'Counted from the last access time and expired beyond this time threshold',
      cacheExpireTtl: 'Time to live TTL(Second)',
      cacheExpireTtl_description:
        'Counted from the time put in, and expire beyond this time threshold',
      auditRequestBody: 'Audit request body',
      auditRequestBodyMaxSize: 'Max bytes(Request)',
      auditRequestEncoding: 'Request body encoding',
      auditResponseBody: 'Audit response body',
      auditResponseBodyMaxSize: 'Max bytes(Response)',
      auditResponseEncoding: 'Response body encoding',
      auditRequestHeaders: 'Audit request headers',
      auditRequestHeadersList: 'Request header list',
      auditRequestHeadersList_description: 'Seperated byy comma.\r\nEmpty means audit all headers.',
      auditResponseHeaders: 'Audit response headers',
      auditResponseHeadersList: 'Response header list',
      auditResponseHeadersList_description:
        'Seperated byy comma.\r\nEmpty means audit all headers.',
      auditQueryString: 'Audit query string',
      auditInterceptors: 'Audit interceptors',
      auditProcess: 'Audit process info',
      portalJWTSecret: 'JWT secret',
      portalJWTSecret_description: 'Random string will be generated if it is not set',
      api: {
        name_description: 'API name, and also the generated process name',
        description_description: 'API description,and also the generated process description',
        apiType: 'Type',
        apiType_options:
          'DB:Database,SAP_RFC:SAP RFC,MAPPING:Mapping,FORWARD:HTTP request forward,PROCESS:Process',
        method: 'Method',
        method_options:
          '*:All,GET,POST,PUT,DELETE,OPTIONS,HEAD,CONNECT,TRACE,PATCH,MOVE,COPY,LINK,UNLINK,WRAPPED',
        uri: 'URI',
        uri_description:
          'Used to invoke this API<br>Start with  / , if it is not start with / system will automatically add / during saving',
        apiErrorHandler: 'Mode',
        apiErrorHandler_options: 'inherit:Inherit from project config,specific:API specific',
        //sqlMode:'Mode',
        mapping: {
          tabName: 'Output',
          contentType: 'Content type',
          content: 'Content'
        },
        process: {
          contentToPayload: 'HTTP content to payload',
          processErrorHandler: 'Process Error Handler',
          processErrorHandler_options:
            'ERROR500:500 error,IGNORE:Ignore error and process provide response'
        }
      },
      infoTabConfig: {
        title: 'Info parameter',
        description:
          'They will be shown in transaction monitor user interface to simplify the search and monitor\n' +
          ' Normally they are used to show some important fields,such as order no#' +
          'Please note:Info1 to info4 are used by system, do not use.'
      }
    },
    interceptorChooser: {
      saveToSet: 'Interceptors can be set after saved.',
      tips: 'Please choose itnerceptors from left. Please note the interceptors will be executed by order.Prehandler will be execute by the order of API, tag and solution,Post processing is just the opposite. Those following the same interceptor will be automatically ignored and not executed.'
    },
    OpenApiViewer: {
      title: 'Open API viewer',
      tab_view: 'View and trial',
      tab_code: 'Open API code'
    },
    Oas3Upload: {
      title: 'Import OAS3 format file',
      itemLabel: 'OAS3 file',
      choose_file: 'Choose file',
      clear_file: 'Clear file',
      submitUpload: 'Start upload',
      submitUpload_tip: 'Please choose file to upload, only one file is allowed.',
      prefixMatch:
        'Match prefix(External system need to add this prefix while invokign,for example /v2)',
      prefixTarget:
        'Prefix of backend system(This prefix will be automatically added after the server IP and port when calling the back-end system)',
      override: 'Override(If choosed the existing data will be automatically override)',
      noFile: 'No file is choosed',
      uploadSuccess: 'Upload success',
      uploadFail:
        'Version error,only OAS3 format JSON file is supported. Other version should be converted first with other tools,such as  https://editor.swagger.io/',
      uploadFail_unkown: 'Unrecognized code returned'
    },
    ApiSchema: {
      tip: 'The schemas defined here can be shared by all APIs in this solution.'
    },
    ApiDummy: {
      description: 'No project is choosed,please press the below button to ',
      addProject: 'Add project'
    },
    ApiList: {
      version: 'Version',
      tag: 'Tag',
      buttonsConfig: {
        batchBorn: 'Batch generate OAS',
        batchDelete: 'Batch delete',
        addAPI: 'Add API'
      },
      versionConfig: {
        version: 'Version',
        version_description: 'Set by user, the system will Not do any validation',
        switchVersion: 'Switch version',
        versionTime: 'Generate time'
      },
      apiVersion: {
        title: 'API version',
        tab_add: 'Add version',
        tab_history: 'Version history',
        tip_add:
          'Save the current snapshot as a version, and switch between historical versions if necessary',
        tip_history:
          'List the historical versions. Click Switch Version to switch back to the selected version',
        switch_confirm:
          'Do you confirm to swith to the selected version? This operation can not be callback!',
        switch_success: 'Version switch successfully,please refresh to view the last data.',
        delete_confirm: 'Do you want to delete this version? This operation can not be callback!',
        delete_success: 'Delete successfully'
      }
    },
    schemaEditor: {
      request: {
        in: 'In',
        required: 'Required'
      },
      MultipleItemsEditor: {
        detailBack: 'Back to list without save',
        detailSave: 'Save and return',
        valdiateFail: 'Data validation failed, please check'
      },
      OpenApiEditorResponseDetail: {
        httpCode: 'HTTP code',
        responseBody: 'Response body'
      },
      PropertyEditor: {
        sample: 'Sample',
        subType: 'Sub type',
        enum: 'Enum',
        match: 'Matching',
        match_placeholder: 'Regular expression',
        minLength: 'Min length',
        maxLength: 'Max length',
        minimum: 'Minimum',
        maximum: 'Maximum',
        multipleOf: 'Multiple of',
        namePara: 'Parameter'
      },
      PropertyListEditor: {
        addProperty: 'Add property'
      },
      OpenApiEditor: {
        title: 'Open API editor',
        buttons: {
          born: 'Re-generate OAS',
          saveClose: 'Save and close'
        },
        tabs: {
          requestPara: 'Request parameter',
          requestBody: 'Request body',
          requestBody_tip: 'It does Not make sense for http method GET and HEAD.',
          response: 'Response',
          schema: 'Schema',
          oadData: 'OAS data',
          oadData_tip1: 'Please save first,and then click',
          oadData_tip2: 'Refresh',
          oadData_tip3: ' to get updated OAS data. Click',
          oadData_tip4: 'Download',
          oadData_tip5: ' to download OAS data.',
          oasTrial: 'OAS trial.'
        },
        loadSuccess: 'Load sucessfully',
        born_prompt1: 'Do you want to re-generate OAS?',
        born_prompt2: 'This API support auto genreate OAS, do you want to generate?',
        bornSuccess: 'OAS generate successfully',
        confirm_close: 'Data is changed, do you want to exit without saving?'
      }
    }
  },
  solutionRms: {
    title: 'Receive Mapping Send Solution',
    related: 'Related',
    receiveType: 'Receive type',
    receiveType_description:
      'Type of receiving data from external system.<br>The parameter is different for different type',
    mappingType: 'Mapping type',
    mappingType_description:
      'If no mapping,data will send directly<br>Otherwise the mapped data will be sent out',
    sendType: 'Send type',
    sendType_description:
      'Type of sending data to external system.<br>The parameter is different for different type',
    content: 'Content',
    content_description:
      'This solutoin is used to receive data from external system, mapping(optional) and then send out.<br>' +
      'It simplify the entry and process configuration<br>' +
      'The generated entry and process can view or edit in proper modules',
    unsupport: 'Unsupported',
    receiveUnsupport: 'Unsuported receive type, maybe the module is not installed or licensed',
    sendUnsupport: 'Unsuported send type, maybe the module is not installed or licensed',
    as2_para_value:
      'm:"Compress:"+(p.get("COMPRESSED")==null?"Unkown":"true"==p.get("COMPRESSED")?"Yes":"No") +",Encrypt:"+(p.get("ENCRYPTED")==null?"Unkown":"true"==p.get("ENCRYPTED")?p.get("ENCRYPT_ALGORITHM"):"No") +",Sign:"+(p.get("SIGNED")==null?"Unknown":"true"==p.get("SIGNED")?p.get("SIGN_ALGORITHM"):"No")',
    receiveTypes: {
      filePollingService: 'File polling',
      filePollingService_filePathFull: 'Receive file name',
      ftpPollingEntry: 'FTP polling',
      ftpPollingEntry_ftpDownloadPath: 'Download path',
      sftpPollingEntry: 'SFTP polling',
      sftpPollingEntry_sftpDownloadPath: 'Receive full file name',
      as2ListenerService: 'AS2 listener',
      as2ListenerService_as2PayloadFileName: 'Rceve payload file name',
      as2ListenerService_as2PayloadContentType: 'Receive content type',
      as2ListenerService_mic: 'Receive MIC',
      as2ListenerService_para: 'Receive AS2 para',
      as2ListenerService_as2MdnMode: 'Receive MDN mode',
      emailPollingService: 'Email polling',
      emailPollingService_fileName: 'Receive file name',
      dbPollingEntry: 'Database polling',
      dbPollingEntry_dbCount: 'Rows count',
      dbPollingEntry_dbSpec: 'Data spec'
    },
    mappingConfig: {
      mapping: 'Mapping',
      paraTable: 'Input paramter'
    },
    mappingTypes: {
      none: 'No mapping',
      mapping: 'Mapping'
    },
    sendTypes: {
      none: 'Not send',
      fileSend: 'File send',
      fileSend_result: 'Send file name',
      ftpSend: 'FTP send',
      ftpSend_result: 'Send file name',
      ftpSend_ftpUploadPath: 'Upload path',
      sftpSend: 'SFTP send',
      sftpSend_result: 'Send file name',
      sftpSend_sftpUploadPath: 'Upload path',
      as2Send: 'AS2 send',
      as2Send_result: 'Send AS2 ID',
      as2Send_mic: 'Send MIC',
      as2Send_para: 'Send AS2 para',
      as2Send_as2MdnMode: 'Send MDN mode',
      emailSend: 'Send email',
      emailSend_result: 'Send email ID',
      jmsSend: 'JMS Send',
      jmsSend_result: 'Send JMS message id'
    },
    Wizard: {
      title: 'Receive Mapping Send  wizard',
      Basic: 'Basic',
      Receive: 'Receive',
      Mapping: 'Mapping',
      Send: 'Send',
      Info: 'Info parameter'
    }
  },
  solutionAuth: {
    related: 'Related',
    save_confirm:
      'This operation will restart HTTP server and display error(just ignore),continue?After restart,you need to login again',
    title: 'System setting',
    adminApiPanel: 'Admin UI API setting',
    entryApiHost: 'Binding IP',
    entryApiHost_description:
      'For security reason, normally it is set to 127.0.0.1 to avoid external access directly',
    entryApiPort: 'Port',
    entryApiPort_description: 'Internaly use only, can be any port',
    adminUiPanel: 'Admin front-end UI setting',
    entryAccessHost: 'Binding IP',
    entryAccessHost_description: 'Empty means binding all IP',
    entryAccessPort: 'Port',
    entryAccessPort_description: 'Admin console port,default value is 9999',
    useNewAdmin: 'Use new UI',
    useNewAdmin_description: 'Explorer cache should be cleared to take affect',
    portalPanel: 'API portal setting',
    portalEnabled: 'Enabled',
    portalEnabled_description: 'API portal can only be accessed if it is enabled here',
    portalAccessHost: 'Binding IP',
    portalAccessHost_description: 'Empty means binding all IP',
    portalAccessPort: 'Port',
    portalAccessPort_description: 'API portal port门,default value is 9998',
    i8nPanel: 'Internationalization',
    i18nEnabled: 'Support',
    i18nDefault: 'Default language',
    i18nDefault_options: 'zh:Chinese,en:English',
    cachePanel: 'Login cache setting',
    cacheType: 'Cache type',
    cacheType_description:
      'Use to store token<br>Ehcache:Local storage,Redis:Remote storage<br>Redis should be used in cluster',
    cacheType_options: 'ehcache:Ehcache(Local),redis:Redis(Remote)',
    cacheEhcacheHeap: 'In heap memory',
    cacheEhcacheHeap_description: 'Number of objects stored in memory',
    cacheRedisConn: 'Redis connection',
    cacheExpireTti: 'Time to idle TTI(Second)',
    cacheExpireTti_description:
      'Counted from the last access time and expired beyond this time threshold',
    cacheExpireTtl: 'Time to live TTL(Second)',
    cacheExpireTtl_description:
      'Counted from the time put in, and expire beyond this time threshold',
    sysCheckPanel: 'System health check',
    sysCheckInterval: 'Check interval(Minute)',
    checkChannel: 'Check channel',
    channelThreshold: 'Alert threshold',
    checkProcess: 'Check process',
    processThreshold: 'Alert threshold(Minute)',
    checkCPUandMem: 'Check CPU and memory',
    cpuThreshold: 'CPU usage',
    memSystemThreshold: 'System memory usage',
    memVMThreshold: 'VM memory usage',
    housekeepingPanel: 'House keeping',
    schedulerHK_description: 'If not set house keeping will be diabled',
    auditHK_enabled: 'Transaction data',
    auditHK_filterThreshold: 'Retention days',
    auditHK_histNameSuffix: 'Collection suffix',
    alertHK_enabled: 'Alert data',
    alertHK_filterThreshold: 'Retention days',
    alertHK_histNameSuffix: 'Collection suffix',
    payloadHK_enabled: 'Payload archive',
    payloadHK_filterThreshold: 'Retention days',
    accessLogHK_enabled: 'Access log',
    accessLogHK_filterThreshold: 'Retention days',
    accessLogHK_histNameSuffix: 'Collection suffix',
    apiAuditHK_enabled: 'API monitor',
    apiAuditHK_filterThreshold: 'Retention days',
    apiAuditHK_histNameSuffix: 'Collection suffix',
    accountSecurityPanel: 'Account security',
    passwordStrengthRequired: 'Password strength required',
    passwordStrengthRequired_description:
      'Max score is 100.\nRefer to user manual for more detail.',
    passwordExpireDays: 'Password expire days',
    passwordExpireDays_description: 'Set 0 to disable this feature',
    passwordChangeForNew: 'New user change password',
    loginCount: 'Login retry times',
    loginCount_description:
      'Set 0 to disable this feature.\nPrinciple will be locked once exceed login retry times',
    loginPeriod: 'login period',
    loginPeriod_description: 'Time window to count the login retry times',
    loginExpire: 'Login expire(Second)',
    loginExpire_description: 'Need to relogin after this period',
    accessLogEnabled: 'Access log enabled',
    logLevelStrategy: 'Show full error while login fail',
    logLevelStrategy_description: 'Uncheck to raise security level',
    auditInfoPanel: 'Tranaction monitor info fields default setting',
    themePanel: 'Theme',
    navBarBackgroundColor: 'Top background color',
    systemName: 'System name'
  },
  reportFlow: {
    item: 'Item',
    count: 'Quantity',
    percentage: 'Percentage',
    countError: 'Error quantity',
    errorRate: 'Error rate',
    mode_options:
      'status:Status,processName:Process,domain:Domain,sender:Sender,receiver:Receiver,subject:Subject,docType:Doc type,-month:Month,-dayOfMonth:Day,-hour:Hour',
    timeStart: 'Time start',
    timeEnd: 'Time end'
  },
  reportPerformance: {
    item: 'Item',
    count: 'Quantity',
    countGood: 'Quantity(Process time less than 60 minutes)',
    percentage: 'Percentage',
    timeStart: 'Time start',
    timeEnd: 'Time end'
  },
  reportMonthCompare: {
    count: 'Quantity',
    countLast: 'Quantity of last month',
    percentage: 'Percentage increase',
    year: 'Year',
    month: 'Month',
    mode_options:
      'status:Status,processName:Process,domain:Domain,sender:Sender,receiver:Receiver,subject:Subject,docType:Doc type'
  },
  reportTraffic: {
    count: 'Count',
    avgPayload: 'Average payload',
    totalPalyoad: 'Total payload',
    avgTimeCost: 'Average time cost(ms)'
  },
  reportTrafficByStatus: {
    total: 'Total',
    SUCCESS: 'Success',
    FAIL: 'Fail',
    ABORT: 'Abort',
    RUN: 'Running',
    RETRY: 'Retrying',
    errorRate: 'Error percentage'
  },
  reportHealth: {
    time: 'Time period',
    time_options: 'hour:Hour,dayOfMonth:Day,month:Month,year:Year',
    cpu_percent: 'CPU average',
    cpu_percent_max: 'CPU max',
    mem_percent: 'Memory average',
    mem_percent_max: 'Memory max',
    mem_vm_percent: 'VM memory average',
    mem_vm_percent_max: 'VM memory max',
    networkSpeedRecv: 'Receive speed average',
    networkSpeedRecv_max: 'Receive speed max',
    networkSpeedSent: 'Send speed average',
    networkSpeedSent_max: 'Send speed max'
  },
  event: {
    title: 'Event setting',
    sequence: 'Sequence',
    mode: 'Trigger mode',
    mode_options: 'SIMPLE:Simple,CUMULATE:Cumulate',
    maxCount: 'Max count',
    maxPeriod: 'Max period(Second)',
    criteriaType: 'Match type',
    criteriaType_options: 'RULES:Rule,SCRIPT:Script',
    criteriaScript: 'Match script',
    criteriaRules: 'Match rules',
    criteriaRules_operator: 'Operator',
    criteriaRules_operator_options:
      '=:Equal,!=:Not equal,in:Include,notIn:Not include,regex:REGEX,isEmpty:Empty,notEmpty:Not empty',
    operation: 'Event operation'
  },
  deploy: {
    please_choose: ' Please choose the file to deploy',
    deploy_button: 'Upload and deploy',
    deploy_result: 'Deploy result',
    result_text0: ' lines',
    result_text10: 'Folder list,totally ',
    result_text11: 'No folder is deployed',
    result_text20: 'Action lis,totally ',
    result_text21: 'No action is deployed',
    result_text30: 'Service lis,totally ',
    result_text31: 'No service is deployed',
    result_text40: 'Resouce list,totally ',
    result_text41: 'No resource is deployed',
    result_text50: 'Plugin list, totally ',
    result_text51: 'No plugin is deployed',
    result_text60: 'Exception list, totally ',
    result_text61: 'No exception',
    status_new: 'Insert',
    status_replace: 'Replace',
    status_ignore: 'Ignore',
    status_suppress: 'Suppress',
    suppress: 'Suppress',
    unsuppress: 'Clear',
    suppressConfirm: 'Do you confirm to suppress',
    unsuppressConfirm: 'Do you confirm to clear suppress',
    tab_new: 'New deploy',
    tab_existing: 'Existing deploy',
    tab_suppress: 'Suppressed deploy'
  },
  migration: {
    nameMatch: 'Name match',
    please_choose: 'Please select the lines to export',
    choose_file: 'Choose file',
    clear_file: 'Clear file',
    view_file: 'View content',
    migrate_file: 'Upload and update',
    migrate_success: 'Migrate successfully',
    exportPanel: 'Config export',
    importPanel: 'Config import',
    please_input_filter: 'Please input filter',
    choosed: 'Choosed',
    unchoosed: 'Unchoosed',
    configFolderMode: {
      label: 'Config folder mode',
      option1: 'Keep orginal',
      option2: 'Reset to default',
      option3: 'Use current'
    }
  },
  uploadTest: {
    file: 'File',
    choose_file: 'Choose file',
    clear_file: 'Clear file',
    upload: 'Upload and test',
    upload_prompt: 'Please choose file(s) and upload, multiple files one time are supported',
    pipeline: 'Pipeline data',
    error_no_file: 'Please choose file',
    error_no_entry: 'No entry is selected',
    test_success: 'Upload and test successfully'
  },
  profile: {
    title: 'Password change',
    changePassword: 'Change password',
    username: 'Username',
    password: 'Password',
    newPassword: 'New password',
    newPassword1: 'Repeat password',
    password_invalid:
      'The password must be numbers, lowercase letters, uppercase letters and special symbols, including at least three types, with a length of 8-30 bits',
    error_not_match: 'New passwords are not matched',
    error_user_not_exist: 'User does not exist',
    error_password_mismatch: 'Old password is not matched',
    error_password_strength: 'New password is too weak'
  },
  accessLog: {
    insertTime: 'Time',
    data: {
      hist: 'History',
      timeStart: 'Start time',
      timeEnd: 'End time',
      url: 'URL',
      rh: 'Remote host',
      ra: 'Remote IP',
      rp: 'Remote port',
      ai: 'User ID',
      an: 'User name'
    }
  },
  dmAudit: {
    hist: 'History',
    startTimeFrom: 'Start time from',
    startTimeTo: 'Start time to',
    dialog: {
      title: 'API monitor detail',
      basic: 'Basic',
      exception: 'Error detail',
      requestBody: 'Request body',
      requestHeaders: 'Request headers',
      responseBody: 'Response body',
      responseHeaders: 'Response headers',
      interceptorResult: 'Interceptor execute',
      processInfo: 'Process info'
    },
    interceptorResult: {
      type: 'Type',
      name: 'Name',
      status: 'Status',
      exception: 'Exception',
      type_prehandler: 'Pre handler',
      type_posthandler: 'Post handler',
      status_success: 'Success',
      status_fail: 'Fail',
      status_abort: 'Abort',
      detail: 'Excetion detail'
    },
    data: {
      startTime: 'Start time',
      endTime: 'End time',
      timeConsume: 'Time consume(ms)',
      direction: ' Direction',
      solution: 'Solution',
      solutionDescription: 'Solution description',
      api: 'API',
      apiDescription: 'API description',
      apiType: 'API type',
      apiMethod: ' API method',
      apiUri: 'API URI',
      queryString: 'Query String',
      authName: ' Authority',
      authPrincipal: 'Principal',
      requestSize: 'Request size',
      responseSize: 'Response size',
      url: 'Full URL',
      rh: 'Remote host',
      ra: 'Remote address',
      rp: 'Remote port',
      status: 'Status',
      httpCode: 'HTTP code',
      server: 'Server',
      error: 'Error detail'
    }
  },
  rn: {
    data: {
      owner: 'Owner',
      partner: 'Partner',
      pipCode: 'PIP code',
      pipName: 'PIP name',
      pipVersion: 'PIP version',
      initiator: 'Initiator',
      rnifVersion: 'RNIF version',
      pipInstanceId: 'PIP instance ID',
      globalUsageCode: 'Production/Test',
      responseMode: 'Response Mode',
      pipType: 'PIP type',
      signStrategy: 'Sign strategy',
      signAlgorithm: 'Sign algorithm',
      encryptStrategy: 'Encrypt strategy',
      encryptAlgorithm: 'Encrypt algorithm',
      datatransferEncoding: 'T ransfer encoding',
      encoding: 'Encoding',
      requestID: 'Request ID',
      requestDate: 'Request time',
      requestCount: 'Request count',
      requestLastTime: 'Request last time',
      requestMessageDigest: 'Request digest',
      requestSignalID: 'Request signal ID',
      requestSignalDate: 'Request signal time',
      responseID: 'Response ID',
      responseDate: 'Response time',
      responseCount: 'Response count',
      responseLastTime: 'Response last time',
      responseMessageDigest: 'Response digest',
      responseSignalID: 'Response signal ID',
      responseSignalDate: 'Response signal time',
      owner_id: 'Owner internal ID',
      partner_id: 'Partner internal ID',
      pip_id: 'PIP internal ID',
      timeStart: 'Time start',
      timeEnd: 'Time end'
    },
    dialog: {
      title: 'Rosettanet detail',
      tabs: {
        basic: 'Basic',
        log: 'Log',
        attachment: 'Attachments'
      },
      log: {
        payload: 'Payload',
        logTime: 'Time',
        logType: 'Type',
        logContent: 'Content'
      },
      requestAttachments: 'Request attachments',
      responseAttachments: 'Response attachments',
      attach: {
        payload: 'Payload',
        contentDescription: 'Description'
      }
    }
  },
  reportProcessSingle: {
    stepName: 'Step name',
    stepTime: 'Time cost(ms)'
  },
  configFolder: {
    name: 'Config foler',
    props: 'Extra props',
    props_description: 'Use key label.en/lang.zh to set English/Chinsese name'
  }
}
