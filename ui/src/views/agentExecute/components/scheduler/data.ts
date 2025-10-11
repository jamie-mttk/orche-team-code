import ScheduleTypeChooser from './ScheduleTypeChooser.vue'
import moment from 'moment'

const shortcuts = [
    {
        text: '现在',
        value: () => moment().toDate(),
    },
    {
        text: '1分钟后',
        value: () => moment().add(1, 'minute').toDate(),
    },
    {
        text: '5分钟后',
        value: () => moment().add(5, 'minutes').toDate(),
    },
    {
        text: '一小时后',
        value: () => moment().add(1, 'hour').toDate(),
    },
    {
        text: '二小时后',
        value: () => moment().add(2, 'hours').toDate(),
    },
    {
        text: '明天凌晨0点',
        value: () => moment().add(1, 'day').startOf('day').toDate(),
    },
    {
        text: '明天凌晨2点',
        value: () => moment().add(1, 'day').startOf('day').add(2, 'hours').toDate(),
    },
    {
        text: '后天凌晨0点',
        value: () => moment().add(2, 'days').startOf('day').toDate(),
    },
    {
        text: '后天凌晨2点',
        value: () => moment().add(2, 'days').startOf('day').add(2, 'hours').toDate(),
    },
]

export const scheduleConfig = {
    props: {
        labelWidth: '120px'
    },
    "children": [
        {
            key: 'mode',
            mode: 'dynamic',
            size: 1,
            label: '定时器类型',
            defaultVal: 'FIXED',
            props: {
                component: ScheduleTypeChooser
            }
        },
        {
            key: 'fixedTime',
            mode: 'datetimePicker',
            label: '执行时间',
            mandatory: true,
            size: 1,
            props: {
                readonly: true,
                shortcuts: shortcuts
            },
            bindings: {
                show: "this.data.mode=='FIXED'"
            }
        },
        {
            key: 'startTime',
            mode: 'datetimePicker',
            label: '开始时间',
            size: 1,
            props: {},
            bindings: {
                show: "this.data.mode!='FIXED'"
            }
        },
        {
            key: 'endTime',
            mode: 'datetimePicker',
            label: '结束时间',
            size: 1,
            props: {},
            bindings: {
                show: "this.data.mode!='FIXED'"
            }
        },
        {
            key: 'repeatInterval',
            mode: 'input',
            label: '间隔',
            mandatory: true,
            defaultVal: '10',
            size: 1,
            props: {
                dataType: 'number'
            },
            bindings: {
                show: "this.data.mode=='SIMPLE'"
            }
        },
        {
            key: 'repeatUnit',
            mode: 'select',
            label: '单位',
            mandatory: true,
            defaultVal: 'minute',
            size: 1,
            props: {
                options: 'hour:小时,minute:分,second:秒,milliSecond:毫秒'
            },
            bindings: {
                show: "this.data.mode=='SIMPLE'"
            }
        },
        {
            key: 'repeatCount',
            mode: 'inputNumber',
            label: '重复次数',
            description: '-1代表无限重复;\r\n当设置为其他值时,实际执行次数为设置值+1',
            mandatory: true,
            defaultVal: '-1',
            size: 1,
            props: {
                min: -1,
                precision: 0
            },
            bindings: {
                show: "this.data.mode=='SIMPLE'"
            }
        },
        {
            key: 'cronExpression',
            mode: 'cron',
            label: 'CRON表达式',
            mandatory: false,
            size: 1,
            bindings: {
                show: "this.data.mode=='CRON'"
            }
        },
    ]
}