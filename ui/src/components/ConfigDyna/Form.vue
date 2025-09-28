<template>
  <el-form ref="dataEditForm" :model="data" v-bind="formAttr" v-show="isShow">
    <el-row :gutter="gutter">
      <template :key="c.key" v-for="c in props.config.children">
        <el-col :span="childSize(c)" v-show="isShowFunc(c)">
          <!--普通组件-->
          <component v-if="!isSlot(c) && !isWrap(c)" :is="wrapType(c)" :config="childConfig(c)" :data="data"
            :expertMode="props.expertMode"></component>
          <!--MTTK wrap组件-->
          <template v-if="isWrap(c)">
            <MttkWrapComp :config="wrapConfig(c)"></MttkWrapComp>
          </template>
          <!--插槽-->
          <CdBase :config="childConfig(c)" :data="data" v-if="isSlot(c)">
            <slot :name="'slot_' + c.key" v-bind:config="c" v-bind:data="data">
              <div v-if="c.description" v-html="c.description" style="display: inline-block; line-height: 24px"></div>
            </slot>
          </CdBase>
        </el-col>
      </template>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
/**
 * form在props中支持如下属性
 * size:尺寸,缺省为small
 * helpMode:显示description的方式
 *          help          在label后面增加图标,移动上去后显示 (缺省)
 *          placeholder   作为控件的placeholder显示
 *          none          不显示
 */
import { ref, computed } from 'vue'
import type { ContainerProps } from './support'
import { useSupport } from './support'
import { MttkWrapComp } from 'mttk-vue-wrap'
import CdBase from './Base.vue'

//
const props = defineProps<ContainerProps>()
//

const { getPropSafe, isShow, childSize, wrapType, isShowFunc } = useSupport(props)
//computed
const data = computed(() => props.data)
//
const gutter = computed(() => {
  return getPropSafe('gutter') || 0
})
//帮助显示方式
const helpMode = computed(() => {
  return getPropSafe('helpMode') || 'help'
})
const formAttr = computed(() => {
  const defaultAttr = { labelWidth: '220px', size: 'small', hideRequiredAsterisk: false }
  return { ...defaultAttr, ...(props.config.props || {}) }
})
//设置子节点的配置,主要是根据FORM设置设置其helpMode
const childConfig = computed(() => {
  return function (c) {
    if (!c.props) {
      c.props = {}
    }
    if (!c.props.helpMode) {
      c.props.helpMode = helpMode.value
    }
    return c
  }
})
//是否是slot
const isSlot = computed(() => {
  return function (child) {
    return 'slot' == child.mode
  }
})
//是否是mttk wrap
const isWrap = computed(() => {
  return function (child) {
    return 'wrap' == child.mode
  }
})
//wrap config
const wrapConfig = computed(() => (c: any) => {
  const result = { '~modelValue': data, ...(childConfig.value(c).wrapConfig || {}) }
  if (!genBase.value(c)) {
    return result
  }
  //
  return {
    '~': CdBase,
    config: childConfig.value(c),
    data: data,
    '#': result
  }
})
//是否生成Base部分
const genBase = computed(() => {
  return function (child) {
    const genBase = child.props?.genBase

    if (genBase == undefined) {
      return true
    }
    return !!genBase
  }
})
//
const dataEditForm = ref()
function validate(callback: Function) {
  return dataEditForm.value.validate(callback)
}

//
defineExpose({ validate })
</script>
