import { computed, isRef, unref, onMounted } from 'vue'

// import type { ExtractPropTypes } from 'vue'
//
import CdInput from './Input.vue'
import CdInputNumber from './InputNumber.vue'
import CdEditor from './Editor.vue'
import CdSelect from './Select.vue'
import CdCheckbox from './Checkbox.vue'
import CdButton from './Button.vue'
import CdPanel from './Panel.vue'
import CdTab from './Tab.vue'
import CdTable from './Table.vue'
import CdFiles from './Files.vue'
import CdDummy from './Dummy.vue'
import CdHtml from './Html.vue'
import CdCron from './Cron.vue'



import CdColorPicker from './ColorPicker.vue'
import CdDateTimePicker from './DateTimePicker.vue'
// import { useLayoutStore } from '@/stores/layout'

export interface ConfigOnlyProps {
  config: object
}

export interface BaseProps extends ConfigOnlyProps {
  data: object
  disabledParent?: boolean
}
export interface ContainerProps extends BaseProps {
}
// export type BaseProps = ExtractPropTypes<typeof minxinBaseProps>

// import type {MixinBaseProps} from './support'
export function useSupport(props: ConfigOnlyProps | BaseProps | ContainerProps) {
  //

  //************************************************* */
  //* Methods
  //************************************************* */

  function getPropSafe(key: string) {
    //安全的到prop里的值
    if (unref(props.config)?.props) {
      return unref(props.config).props[key]
    } else {
      return undefined
    }
  }
  function getPropWithDefault(key: string, defaultVal: any) {
    const val = getPropSafe(key)
    if (val == undefined) {
      return defaultVal
    } else {
      return val
    }
  }
  //Safe eval which consider this.data
  function safeEval(script: string) {
    //This variable is what this refer to
    const targetObj = {
      data: unref(props.data)
    }
    //
    //直接使用eval this不起效
    function dummyEval() {
      return eval(script)
    }
    //
    // console.log('safeEval', script, targetObj.data.mode, dummyEval.call(targetObj))
    //
    return dummyEval.call(targetObj)
  }
  //************************************************* */
  //* Computed
  //************************************************* */
  onMounted(() => {
    //Apply defaut value
    const d = unref(props.data) as any
    //
    if (!d) {
      return
    }
    //
    if (!d.hasOwnProperty(props.config.key)) {
      //There is no value,try to get default value
      if (!props.config.hasOwnProperty('defaultVal')) {
        //There is no defaultValue set ,return undefined directly
        return
      }

      //data has not set,use default
      let val = props.config.defaultVal
      if (props.config.mode == 'checkbox') {
        if (typeof val === 'string') {
          val = safeEval(val.toLowerCase())
        }
      } else if (isNumeric.value) {
        const n = parseFloat(val)
        val = isNaN(n) ? val : n
      }
      //
      d[props.config.key] = val
      //
    }
  })
  //
  const modelValue = computed({
    get: () => {
      const d = unref(props.data) as any

      if (!d) {
        return undefined
      }

      return d[props.config.key]
    },
    set: (val) => {
      //如果以.结尾时以为者用户还希望输入小数点后面的值
      //如果不做此判断会发现小数点无法输入
      if (isNumeric.value && val && typeof val == 'string' && !val.endsWith('.')) {
        //相当于v-model的.number修饰符
        const n = parseFloat(val)

        val = isNaN(n) ? val : n
      }
      //
      unref(props.data)[props.config.key] = val
    }
  })
  //
  const isShow = computed(() => {
    if (!props.config?.bindings) {
      return true
    }
    let show = unref(props.config.bindings.show)

    if (show == undefined) {
      return true
    }

    if (typeof show == 'boolean') {
      return show
    }
    //build this TBD
    // show='this.'+show;
    show = safeEval(show)
    //确保返回的是true/false而不是js认为时true的,如非空
    if (show) {
      return true
    } else {
      return false
    }
  })
  const isShowFunc = computed(() => showTest)

  function showTest(c: any) {
    if (!c.bindings) {
      return true
    }
    let show = unref(c.bindings.show)
    if (show == undefined) {
      return true
    }
    if (typeof show == 'boolean') {
      return show
    }
    //build this TBD
    // show='this.'+show;
    show = safeEval(show)
    //确保返回的是true/false而不是js认为时true的,如非空
    if (show) {
      return true
    } else {
      return false
    }
  }
  //isEditing : _id不为空
  const isEditing = computed(() => !!props.data._id)
  //处理disabled binding,用于处理是否禁用
  const isDisabled = computed(() => {
    if (props.disabledParent) {

      return true
    }
    if (!props.config?.bindings) {
      return false
    }
    let disabled = props.config.bindings.disabled
    if (disabled == undefined) {
      return false
    }
    if (typeof disabled == 'boolean') {
      return disabled
    }

    disabled = safeEval(disabled)

    if (disabled) {
      return true
    } else {
      return false
    }
  })
  //处理validates
  const rules = computed(() => {
    let rule = undefined
    if (props.config?.mandatory) {
      //
      const trigger = props.config.mode == 'select' ? 'change' : 'blur'
      rule = {
        required: true,
        message: '字段不允许为空',
        trigger: trigger
      }
    }

    const validates = props.config?.validates
    if (validates) {
      //validates.foreach(a-->console.log('AAAA:'+a))
      if (rule) {
        //有mandatory规则可能需要拼接
        const rules = []
        rules.push(rule)
        if (Array.isArray(validates)) {
          for (let i = 0; i < validates.length; i++) {
            rules.push(validates[i])
          }

          return rules
        } else {
          rules
        }
      } else {
        //直接返回显示的规则
        return validates
      }
    } else {
      //如果没有给出validates只需要考虑mandatory的约束
      if (rule) {
        return [rule]
      } else {
        return []
      }
    }
    //
    return []
  })
  //
  //处理dataType props，判断是否是数字类型;如果是返回true,其他情况返回false
  const isNumeric = computed(() => {
    const dataType = getPropSafe('dataType')
    if (dataType == undefined) {
      return false
    }
    return 'number' == dataType
  })
  //控件的placeHolder
  const placeholder = computed(() => {
    //没有props缺省为help;或明确给出是help
    if (
      props.config?.description &&
      props.config?.props &&
      props.config?.props?.helpMode == 'placeholder'
    ) {
      return props.config.description
    } else {
      return ''
    }
  })
  //Below are for container mixin
  const wrapType = computed(() => (child: any) => {
    const mode = child.mode
    if (!mode || 'input' == mode) {
      return CdInput
    } else if ('inputNumber' == mode) {
      return CdInputNumber
    } else if ('editor' == mode) {
      return CdEditor
    } else if ('select' == mode) {
      return CdSelect
    } else if ('checkbox' == mode) {
      return CdCheckbox


    } else if ('datetimePicker' == mode) {
      return CdDateTimePicker
    } else if ('files' == mode) {
      return CdFiles
    } else if ('colorPicker' == mode) {
      return CdColorPicker
    } else if ('cron' == mode) {
      return CdCron
    } else if ('button' == mode) {
      return CdButton
    } else if ('panel' == mode) {
      return CdPanel
    } else if ('tabFolder' == mode) {
      return CdTab
    } else if ('table' == mode) {
      return CdTable


    } else if ('dummy' == mode) {
      return CdDummy
    } else if ('html' == mode) {
      return CdHtml
    } else if ('dynamic' == mode) {
      return child.props.component
    } else {
      //console.log("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" + child);
      //Unsupported
      return ''
    }
  })
  const childSize = computed(() => (child: any) => {
    //
    // const layoutStore = useLayoutStore()
    // if (!layoutStore.isLargeScreen) {
    //   return 24
    // }
    if (child && child.size) {
      const size = unref(child.size)
      if (size > 0) {
        const val = Math.floor(24 / size)
        return val <= 0 ? 1 : val
      } else if (size < 0) {
        const val = -1 * size
        return val > 24 ? 24 : val
      }
    }
    //缺省是满行
    return 24
  })
  //Whether to show help button
  const showHelpButton = computed(() => {
    //没有描述不显示
    if (!props.config?.description) {
      return false
    }
    //没有props缺省为help;或明确给出是help
    return (props.config?.props?.helpMode || 'help') == 'help'
  })
  //show label
  const showLabel = computed(() => {
    const showLabel = getPropSafe('showLabel')

    return showLabel || 'true' == showLabel || showLabel == undefined || showHelpButton.value
  })

  //
  return {
    isShow,
    isShowFunc,
    isEditing,
    isDisabled,
    rules,
    isNumeric,
    placeholder,
    modelValue,
    getPropSafe,
    getPropWithDefault,
    wrapType,
    childSize,
    showHelpButton,
    showLabel
  }
}
