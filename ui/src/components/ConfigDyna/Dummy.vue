<template>
  <CdBase :config="props.config" :data="props.data"></CdBase>
</template>
<script setup lang="ts">

import type { BaseProps } from './support'

import CdBase from './Base.vue'
const props = defineProps<BaseProps>()

</script>