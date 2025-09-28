<template>
  <CdBase :config="props.config" :data="props.data">
    <el-input
      v-model="modelValue"
      :type="inputType"
      :rows="inputRows"
      :placeholder="placeholder"
      :disabled="isDisabled"
      :clearable="true"
      autocomplete="new-password"
    >
    </el-input>
  </CdBase>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
const props = defineProps<BaseProps>()
const { modelValue, placeholder, isDisabled, getPropSafe } = useSupport(props)

//
const inputType = computed(() => {
  let thisType = getPropSafe('type')
  if (!thisType) {
    return 'text'
  }
  if ('multiple' == thisType || 'textarea' == thisType) {
    return 'textarea'
  } else if ('password' == thisType) {
    return 'password'
  } else {
    return thisType
  }
})
//针对多行文本，返回行数
const inputRows = computed(() => {
  if ('multiple' == getPropSafe('type')) {
    var rows = getPropSafe('rows')
    if (rows) {
      return rows
    } else {
      return 3
    }
  } else {
    return ''
  }
})
//
// const autocomplete = computed(() => {
//   if ('password' == inputType.value) {
//     return 'new-password'
//   } else {
//     return 'new-password'
//     // return 'off'
//   }
// })
</script>
