<template>
  <CdBase :config="props.config" :data="props.data">
    <el-switch v-model="modelValue" :disabled="isDisabled"></el-switch>
  </CdBase>
</template>

<script setup lang="ts">
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
const props = defineProps<BaseProps>()
const { modelValue, isDisabled } = useSupport(props)
</script>
