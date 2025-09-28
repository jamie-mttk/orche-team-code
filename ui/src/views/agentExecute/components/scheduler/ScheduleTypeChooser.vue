<template>

  <CdBase :config="props.config" :data="props.data">
    <el-radio-group v-model="modelValue">
      <el-radio-button label="执行一次" value="FIXED" />
      <el-radio-button label="周期执行" value="SIMPLE" />
      <el-radio-button label="CRON" value="CRON" />
    </el-radio-group>
  </CdBase>
</template>

<script setup lang="ts">
import type { BaseProps } from '@/components/ConfigDyna/support'
import CdBase from '@/components/ConfigDyna/Base.vue'
import { useSupport } from '@/components/ConfigDyna/support'

//
const props = defineProps<BaseProps>()
const { modelValue } = useSupport(props)
</script>
