<template>
  Start component<br />
  <el-input v-model="modelValue"></el-input>
  End component<br />
</template>
<script setup lang="ts">
import type { BaseProps } from './support'
import { useSupport } from './support'

const props = defineProps<BaseProps>()
const { modelValue } = useSupport(props)
console.log('########', props.config)
</script>
