<template>
  <CdBase :config="props.config" :data="props.data">
    <span v-html="realValue"></span>
  </CdBase>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'

const props = defineProps<BaseProps>()
const { modelValue } = useSupport(props)
//
const realValue = computed(() => modelValue.value)
</script>
