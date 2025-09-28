<template>
  <CdBase :config="props.config" :data="props.data">
    <el-popover :visible="cronPopover" width="900px">
      <noVue3Cron :cron-value="modelValue" @change="handleChange" @close="closePopover" :i18n="lang"></noVue3Cron>
      <template #reference>
        <el-input type="text" v-model="modelValue" :placeholder="placeholder" :disabled="isDisabled" :clearable="true">
          <template #append>
            <el-button @click="showPopover">...</el-button>
          </template>
        </el-input>
      </template>
    </el-popover>
  </CdBase>
</template>
<script setup lang="ts">
import { ref, computed, unref } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
import { noVue3Cron } from 'no-vue3-cron'
import 'no-vue3-cron/lib/noVue3Cron.css'


const props = defineProps<BaseProps>()
const { modelValue, isDisabled, placeholder } = useSupport(props)

//
const cronPopover = ref(false)
//
function handleChange(val) {
  if (typeof val != 'string') {
    //点击具体秒数等会触发change时间,此时传入的是object,所以这里要忽略掉这些事件
    return
  }
  modelValue.value = val
}
function showPopover() {
  cronPopover.value = true
}
function closePopover() {
  cronPopover.value = false
}
//get en/cn from locale

const lang = 'cn' 
</script>
<style>
/*不显示语言选择按钮 */
#changeContab .language {
  display: none;
}
</style>
