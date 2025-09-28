<template>
  <el-button-group size="small">
    <template v-for="(c, index) in normalButtons" :key="c.key">
      <el-button :type="c.type" :disabled="isDisabled(c)" @click="handleClick(c.key)">
        <mdi-icon v-if="c.icon" :icon="c.icon" size="x-small" style="margin-right: 8px"></mdi-icon>
        {{ c.label }}
      </el-button>
    </template>

    <el-dropdown v-if="moreButtons.length > 0" @command="
      (command) => {
        handleClick(command)
      }
    ">
      <el-button size="small" :disabled="disabled">
        更多
        <mdi-icon icon="mdiChevronDown"></mdi-icon>
      </el-button>
      <template v-slot:dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="c in moreButtons" :key="c.key" :command="c.key" :disabled="isDisabled(c)">
            <mdi-icon v-if="c.icon" :icon="c.icon" size="x-small" style="margin-right: 8px"></mdi-icon>{{ c.label
            }}</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-button-group>
</template>

<script setup lang="ts">
import { ref, computed, unref } from 'vue'

const props = defineProps<{
  config: object
  disabled?: boolean
}>()
const emit = defineEmits<{
  click: [command: string]
}>()

//computed
//是否Disabled
const isDisabled = computed(() => (c: any) => {
  if (typeof c.disabled == 'boolean') {
    return c.disabled
  }
  let disabled = c.disabled ? c.disabled : undefined
  if (disabled == undefined) {
    return false
  }
  //确保返回的是true/false而不是js认为时true的,如非空
  return !!disabled
})
//
const max = computed(() => props.config.max || 100)
//不显示在moreButtons里的按钮
const normalButtons = computed(() => {
  //
  return props.config.list.filter((item, index) => index < max.value)
})
//把所有需要放到"更多"的按钮找出来,如果没有返回[]
const moreButtons = computed(() => {
  //没有设置max或max为负数(无穷大)
  if (!max.value || max.value < 0) {
    return []
  }
  //
  return props.config.list.filter(
    //!isDropDown(c) && isShowDirect(index) && isShow(c)
    (item, index) => index >= max.value
  )
})
//
function handleClick(command: string) {
  emit('click', command)
}
</script>
