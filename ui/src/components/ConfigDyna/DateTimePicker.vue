<template>
  <CdBase :config="config" :data="data">
    <el-time-picker v-if="isTimePicker" :is-range="isTimeRange" v-model="modelValue" :placeholder="placeholder"
      :disabled="isDisabled" size="small" :type="pickerType" :pickerOptions="pickerOptions" start-placeholder="开始时间"
      end-placeholder="结束时间" range-separator="至" :value-format="valueFormat" :format="valueFormat"
      style="width:100%"></el-time-picker>
    <!-- :default-value="defaultTime" -->
    <el-date-picker v-if="!isTimePicker" v-model="modelValue" :placeholder="placeholder" :disabled="isDisabled"
      size="small" :type="pickerType" :pickerOptions="pickerOptions" :shortcuts="shortcuts" start-placeholder="开始日期"
      end-placeholder="结束日期" range-separator="至" :value-format="valueFormat" :format="valueFormat"
      style="width:100%"></el-date-picker>
    <!-- :default-time="defaultTime" -->
  </CdBase>
</template>
<script setup lang="ts">
/**
 * 支持的config
 *  valueFormat: 参考el-date-picker/el-time-picker
    pickerType: time/timerange以及el-date-picker支持的所有类型
    defaultTime:参考el-date-picker/el-time-picker[与pickType相关]
    pickerOptions: 参考el-date-picker/el-time-picker[与pickType相关]
 */
import { computed } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
const props = defineProps<BaseProps>()
const { modelValue, placeholder, isDisabled, getPropSafe } = useSupport(props)
//
const pickerType = computed(() => {
  return getPropSafe('pickerType') || 'datetime'
})
const isTimePicker = computed(() => {
  return pickerType.value == 'time' || pickerType.value == 'timerange'
})
const isTimeRange = computed(() => {
  return pickerType.value == 'timerange'
})
const valueFormat = computed(() => {
  return getPropSafe('valueFormat') || (isTimePicker.value ? 'HH:mm:ss' : 'YYYY-MM-DD HH:mm:ss')
})

const pickerOptions = computed(() => {
  return getPropSafe('pickerOptions') || {}
})
const shortcuts = computed(() => {
  return getPropSafe('shortcuts') || []
})
// const defaultTime = computed(() => {
//   return getPropSafe('defaultTime') || '00:00:00'
// })
</script>
