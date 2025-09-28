<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <span><mdi-icon v-bind="$attrs" icon="mdiTranslate"></mdi-icon> </span>

    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="locale in localeList"
          :key="locale.key"
          :command="locale.key"
          :disabled="localCurrent == locale.key"
        >
          {{ locale.name }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { inject } from 'vue'
import { localeList, changeLocale, localCurrent } from '@/lang/index'
const globalContext = inject('globalContext')
//
defineOptions({
  inheritAttrs: false
})
//
const handleCommand = (command: string) => {
  changeLocale(command, globalContext)
}
</script>
