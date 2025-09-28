<template>
  <el-form-item
    :prop="props.config?.key"
    :rules="rules"
    v-if="isShow"
    :label-width="labelWidth"
    :label-position="labelPosition"
  >
    <template #label
      ><slot name="label"><CdLabel :config="props.config" v-if="showLabel"> </CdLabel> </slot
    ></template>
    <slot></slot>
  </el-form-item>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdLabel from './Label.vue'
const props = defineProps<BaseProps>()
const { rules, isShow, showLabel } = useSupport(props)
//

//
const labelWidth = computed(() => {
  if (props.config && props.config.props && props.config.props.labelWidth) {
    return props.config.props.labelWidth
  } else {
    return ''
  }
})
//
const labelPosition = computed(() => {
  //注意这里是从config获取而不是从config.props
  //这是因为此功能当前只用于table,而table.props下的labelPosition给出了table弹出窗口form的label position

  if (props.config && props.config.labelPosition) {
    return props.config.labelPosition
  } else {
    return ''
  }
})
</script>
