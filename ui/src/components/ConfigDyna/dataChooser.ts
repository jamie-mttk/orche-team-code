//
export const profile = {
  entry: {
    urlOptions: 'entry/query?_belongTo=TYPE_SUB',
    urlUI: 'entry/byKey/TYPE_SUB',
    urlSave: 'entry/save',
    urlDelete: 'entry/delete',
    typeKey: 'belongTo'
  },
  process: {
    urlOptions: 'process/query',
    urlSave: 'process/save',
    urlDelete: 'process/delete'
  },
  pool: {
    urlOptions: 'pool/query?_belongTo=TYPE_SUB',
    urlUI: 'pool/byKey/TYPE_SUB',
    urlSave: 'pool/save',
    urlDelete: 'pool/delete',
    typeKey: 'belongTo',
    urlActive: 'pool/active'
  },
  resource: {
    urlOptions: 'resource/query?_belongTo=TYPE_SUB',
    urlUI: 'resource/byKey/TYPE_SUB',
    urlSave: 'resource/save',
    urlDelete: 'resource/delete',
    typeKey: 'belongTo'
  },
  scheduler: {
    urlOptions: 'scheduler/query',
    urlSave: 'scheduler/save',
    urlDelete: 'scheduler/delete'
  },
  retry: {
    urlOptions: 'retry/query',
    urlSave: 'retry/save',
    urlDelete: 'retry/delete'
  },
  runtimeStrategy: {
    urlOptions: 'runtimeStrategy/query'
  },
  channel: {
    urlOptions: 'dispatch/query'
  },
  mapping: {
    urlOptions: 'mapping/query'
  },
  mapper: {
    urlOptions: 'mapper/query'
  },
  cache: {
    urlOptions: 'cache/defines'
  },
  lookup: {
    urlOptions: 'lookup/defines'
  },
  authentication: {
    urlOptions: 'authentication/defines'
  },
  authorization: {
    urlOptions: 'authorization/defines'
  }
}

export const retryConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '重试策略',
  props: {
    width: '40%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,max:120,interval:120,intervalInc:140'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'max',
      mode: 'inputNumber',
      label: '最大次数',
      mandatory: true,
      props: {
        min: 1,
        max: 100
      }
    },
    {
      key: 'interval',
      mode: 'inputNumber',
      label: '间隔(秒)',
      mandatory: true,
      props: {
        min: 1,
        max: 3600
      }
    },
    {
      key: 'intervalInc',
      mode: 'inputNumber',
      label: '间隔增加(秒)',
      props: {
        min: 0,
        max: 3600
      }
    }
  ]
}

//通用的定时器配置
const commonTrigger = [
  {
    key: 'startTime',
    mode: 'datetimePicker',
    label: '开始时间',
    props: {},
    bindings: {
      show: "this.data.mode!='COMBINATION'"
    }
  },
  {
    key: 'endTime',
    mode: 'datetimePicker',
    label: '结束时间',
    props: {},
    bindings: {
      show: "this.data.mode!='COMBINATION'"
    }
  }
]
//简单定时器配置
const simpleTrigger = {
  key: 'simpleTriggerPanel',
  label: '',
  mode: 'panel',
  bindings: {
    show: "this.data.mode=='SIMPLE'"
  },
  children: [
    {
      key: 'repeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'repeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'hour:小时,minute:分,second:秒,milliSecond:毫秒'
      }
    },
    {
      key: 'repeatCount',
      mode: 'inputNumber',
      label: '重复次数',
      description: '-1代表无限重复;\r\n当设置为其他值时,实际执行次数为设置值+1',
      mandatory: true,
      props: {
        min: -1,
        max: 1000000
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 重复次数为0,MISFIRE_INSTRUCTION_FIRE_NOW; 重复次数为-1,MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT; 其他,MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_NOW. 如果重复次数!=0,等同于 MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;否则,立刻执行一次', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT. 立刻触发一次,总触发次数不变.', _id: '2' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT. 立刻触发一次,总触发次数扣除错过的次数.', _id: '3' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT. 下一个触发点触发,总触发次数不变.', _id: '5' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT. 下一个触发点触发,总触发次数扣除错过的次数.', _id: '4' }
        ]
      }
    }
  ]
}
//CRON定时器配置
const cronTrigger = {
  key: 'cronTriggerPanel',
  label: '',
  mode: 'panel',
  bindings: {
    show: "this.data.mode=='CRON'"
  },
  children: [
    {
      key: 'cronExpression',
      mode: 'input',
      label: 'CRON表达式',
      mandatory: false
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

//每日定时触发器配置
const dailyTrigger = {
  key: 'dailyTriggerPanel',
  label: '',
  mode: 'panel',
  bindings: {
    show: "this.data.mode=='DAILY'"
  },
  children: [
    {
      key: 'dailyRepeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'dailyRepeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'hour:小时,minute:分,second:秒'
      }
    },
    {
      key: 'dailyStartTime',
      mode: 'timePicker',
      label: '每日开始',
      mandatory: true
    },
    {
      key: 'dailyEndTime',
      mode: 'timePicker',
      label: '每日结束',
      mandatory: true
    },
    {
      key: 'dailyDaysOfWeek',
      mode: 'select',
      label: '执行日期',
      mandatory: true,
      props: {
        options: '1:周日,2:周一,3:周二,4:周三,5:周四,6:周五,7:周六'
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

//日历周期触发器配置
const calendarTrigger = {
  key: 'calendarTriggerPanel',
  label: '',
  mode: 'panel',
  bindings: {
    show: "this.data.mode=='CALENDAR'"
  },
  children: [
    {
      key: 'calendarRepeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'calendarRepeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'year:年,month:月,week:周,day:日,hour:小时,minute:分,second:秒'
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

//组合定时器
const combinationTrigger = {
  key: 'triggers',
  mode: 'table',
  props: {
    cols: 'mode,startTime,endTime',
    showTitle: 'false',
    width: '40%',
    labelWidth: '140px'
  },
  bindings: {
    show: "this.data.mode=='COMBINATION'"
  },
  children: [
    {
      key: 'mode',
      mode: 'select',
      label: '模式',
      mandatory: true,
      props: {
        options: 'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期'
      }
    }
  ]
}

export const processAddConfig = {
  label: '添加 ' + '流程',
  hideExpertMode: true,
  props: {
    width: '30%',
    labelWidth: '120px'
  },
  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '2'
      }
    }
  ]
}

export const schedulerConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,mode:120,startTime:160,endTime:160,repeatInterval:120,repeatUnit:120,repeatCount:120,misFire:200'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'startTime',
      mode: 'datetimePicker',
      label: '开始时间',
      mandatory: true
    },
    {
      key: 'endTime',
      mode: 'datetimePicker',
      label: '结束时间'
    },
    {
      key: 'mode',
      mode: 'select',
      label: '模式',
      mandatory: true,
      props: {
        options: 'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期,COMBINATION:组合定时器'
      }
    },
    {
      key: 'repeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'repeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'hour:小时,minute:分,second:秒,milliSecond:毫秒'
      }
    },
    {
      key: 'repeatCount',
      mode: 'inputNumber',
      label: '重复次数',
      description: '-1代表无限重复;\r\n当设置为其他值时,实际执行次数为设置值+1',
      mandatory: true,
      props: {
        min: -1,
        max: 1000000
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 重复次数为0,MISFIRE_INSTRUCTION_FIRE_NOW; 重复次数为-1,MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT; 其他,MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_NOW. 如果重复次数!=0,等同于 MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;否则,立刻执行一次', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT. 立刻触发一次,总触发次数不变.', _id: '2' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT. 立刻触发一次,总触发次数扣除错过的次数.', _id: '3' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT. 下一个触发点触发,总触发次数不变.', _id: '5' },
          { name: 'MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT. 下一个触发点触发,总触发次数扣除错过的次数.', _id: '4' }
        ]
      }
    }
  ]
}

export const cronConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,cronExpression:200,misFire:200'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'cronExpression',
      mode: 'input',
      label: 'CRON表达式',
      mandatory: true
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

export const dailyConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,dailyStartTime:160,dailyEndTime:160,dailyRepeatInterval:120,dailyRepeatUnit:120,dailyDaysOfWeek:120,misFire:200'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'dailyRepeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'dailyRepeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'hour:小时,minute:分,second:秒'
      }
    },
    {
      key: 'dailyStartTime',
      mode: 'timePicker',
      label: '每日开始',
      mandatory: true
    },
    {
      key: 'dailyEndTime',
      mode: 'timePicker',
      label: '每日结束',
      mandatory: true
    },
    {
      key: 'dailyDaysOfWeek',
      mode: 'select',
      label: '执行日期',
      mandatory: true,
      props: {
        options: '1:周日,2:周一,3:周二,4:周三,5:周四,6:周五,7:周六'
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

export const calendarConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,calendarRepeatInterval:120,calendarRepeatUnit:120,misFire:200'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'calendarRepeatInterval',
      mode: 'inputNumber',
      label: '间隔',
      mandatory: true,
      props: {
        min: 1,
        max: 1000000
      }
    },
    {
      key: 'calendarRepeatUnit',
      mode: 'select',
      label: '单位',
      mandatory: true,
      props: {
        options: 'year:年,month:月,week:周,day:日,hour:小时,minute:分,second:秒'
      }
    },
    {
      key: 'misFire',
      mode: 'select',
      label: '错过触发策略',
      description: '当因为某些原因(如服务停止后)错过触发的处理的处理策略.\r\n错过定义的阈值在文件conf/quartz.properties通过参数org.quartz.jobStore.misfireThreshold 定义.',
      mandatory: true,
      props: {
        options: [
          { name: 'MISFIRE_INSTRUCTION_SMART_POLICY. 等同于 MISFIRE_INSTRUCTION_FIRE_NOW', _id: '0' },
          { name: 'MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLIC. 重新触发所有错过的调用', _id: '-1' },
          { name: 'MISFIRE_INSTRUCTION_FIRE_ONCE_NOW. 立刻触发一次,然后按照Cron频率触发', _id: '1' },
          { name: 'MISFIRE_INSTRUCTION_DO_NOTHING.忽略错过,等待下次Cron触发', _id: '2' }
        ]
      }
    }
  ]
}

export const combinationConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,_insertTime:160,_updateTime:160,combinationTrigger_mode:120'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'combinationTrigger_mode',
      mode: 'select',
      label: '模式',
      mandatory: true,
      props: {
        options: 'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期'
      }
    }
  ]
}

export const processConfig = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '添加 流程',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,description,_insertTime:160,_updateTime:160'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    }
  ]
}

export const schedulerConfigForProcess = {
  key: 'list', //list代表从数据的key=list中获取数据
  label: '定时器',
  props: {
    width: '60%',
    labelWidth: '140px',
    cols: 'name,description,_insertTime:160,_updateTime:160,mode:120'
  },

  children: [
    {
      key: 'name',
      mode: 'input',
      label: '名称',
      mandatory: true
    },
    {
      key: 'description',
      mode: 'input',
      label: '描述',
      props: {
        type: 'multiple',
        rows: '1'
      }
    },
    {
      key: '_insertTime',
      mode: 'dummy',
      label: '插入时间'
    },
    {
      key: '_updateTime',
      mode: 'dummy',
      label: '更新时间'
    },
    {
      key: 'mode',
      mode: 'select',
      label: '模式',
      mandatory: true,
      props: {
        options: 'SIMPLE:简单模式,CRON:CRON表达式,DAILY:每日定时,CALENDAR:日历周期,COMBINATION:组合定时器'
      }
    }
  ]
}

//把后续JSON数组合并到第一个
function merge(target, source) {
  if (Array.isArray(source)) {
    for (const item of source) {
      target.push(item)
    }
  } else {
    target.push(source)
  }
  //
  return target
}

//
//这里对schedulerConfig进行了数据补充
//
merge(schedulerConfig.children, commonTrigger)
merge(schedulerConfig.children, simpleTrigger)
merge(schedulerConfig.children, cronTrigger)
merge(schedulerConfig.children, dailyTrigger)
merge(schedulerConfig.children, calendarTrigger)
//组合定时器
merge(combinationTrigger.children, commonTrigger)
merge(combinationTrigger.children, simpleTrigger)
merge(combinationTrigger.children, cronTrigger)
merge(combinationTrigger.children, dailyTrigger)
merge(combinationTrigger.children, calendarTrigger)
merge(schedulerConfig.children, combinationTrigger)
//
//console.log(JSON.stringify(schedulerConfig))
//返回完整的定时器配置
// export function schedulerConfig() {

//   //
//   return config;
// }

export { schedulerConfig }
