<template>

  <div style="display: inline-block" v-if="showLabel">
    <slot>{{ config.label }}</slot>
    <el-popover v-if="showHelpButton" placement="bottom-start" width="400" trigger="hover">
      <div v-html="formatedDescription"></div>
      <template #reference>
        <span>
          <icon name="mdiInformationOutline" size="small"></icon>
        </span>
      </template>
    </el-popover>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { ConfigOnlyProps } from './support'
import { useSupport } from './support'
import Icon from '../mdiIicon/index.vue'

const props = defineProps<ConfigOnlyProps>()
const { showLabel, showHelpButton } = useSupport(props)
//

//格式化描述使其符合help显示方式
const formatedDescription = computed(() => {
  let c = props.config.description
  if (!c) {
    return undefined
  }
  //
  c = c.replace(/\n/g, '<br>')
  //
  return c
})
</script>
