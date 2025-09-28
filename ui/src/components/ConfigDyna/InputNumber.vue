<template>
  <CdBase :config="props.config" :data="props.data">
    <el-input-number
      size="small"
      v-model="modelValue"
      :placeholder="placeholder"
      :disabled="isDisabled"
      :clearable="true"
      :min="getPropSafe('min')"
      :max="getPropSafe('max')"
      :precision="getPropSafe('precision')"
    >
    </el-input-number>
  </CdBase>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
const props = defineProps<BaseProps>()
const { modelValue, placeholder, isDisabled, getPropSafe } = useSupport(props)
</script>
