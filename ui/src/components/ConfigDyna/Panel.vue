<template>
  <div v-if="isShow">
    <!--如果有label显示成带背景的样式,否则直接显示-->
    <el-card v-if="props.config.label" class="box-card" shadow="hover" style="margin: 4px 0px">
      <template v-slot:header>
        <div class="clearfix">
          <CdLabel :config="config"></CdLabel>
        </div>
      </template>
      <el-row>
        <el-col :span="childSize(c)" :key="c.key" v-for="c in props.config.children">
          <component
            :is="wrapType(c)"
            :config="c"
            :data="data"
            :disabledParent="isDisabled"
            :expertMode="expertMode"
          ></component>
        </el-col>
      </el-row>
    </el-card>
    <el-row v-else>
      <el-col :span="childSize(c)" :key="c.key" v-for="c in props.config.children">
        <component
          :is="wrapType(c)"
          :config="c"
          :data="data"
          :disabledParent="isDisabled"
          :expertMode="expertMode"
        ></component>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import type { ContainerProps } from './support'
import { useSupport } from './support'
import CdLabel from './Label.vue'
//
const props = defineProps<ContainerProps>()
//
const { isShow, childSize, wrapType, isDisabled } = useSupport(props)
</script>
