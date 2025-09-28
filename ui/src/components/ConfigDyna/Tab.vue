<template>
  <el-tabs type="border-card" v-if="isShow">
    <el-tab-pane
      v-for="child in config.children"
      :key="child.key"
      :disabled="tabDisabled(child)"
      v-show="false"
    >
      <template v-slot:label>
        <span>
          <CdLabel :config="child"></CdLabel>
        </span>
      </template>
      <el-row>
        <el-col :span="childSize(c)" :key="c.key" v-for="c in child.children">
          <component
            :is="wrapType(c)"
            :config="c"
            :data="data"
            :disabledParent="tabDisabled(child)"
            :expertMode="props.expertMode"
          ></component>
        </el-col>
      </el-row>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ContainerProps } from './support'
import { useSupport } from './support'
import CdLabel from './Label.vue'
//
const props = defineProps<ContainerProps>()
//
const { isShow, childSize, wrapType } = useSupport(props)
//
const tabDisabled = computed(() => {
  return function (child) {
    //
    let disabled = child.bindings ? child.bindings.disabled : undefined
    if (disabled == undefined) {
      return false
    }
    disabled = eval(disabled)
    return disabled
  }
})
</script>
