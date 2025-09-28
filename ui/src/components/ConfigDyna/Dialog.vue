<template>
  <el-dialog v-model="model" v-bind="dialogAttr" @close="handleClose" :close-on-click-modal="false"
    :close-on-press-escape="false">
    <template #header>
      <div class="header-title">
        {{ props.config.label || dialogAttr.title }}
        <mdi-icon icon="mdiMenuRight" @click="showTitleDetail = true"
          v-if="!showTitleDetail && props.data._id"></mdi-icon>
        <mdi-icon icon="mdiMenuLeft" @click="showTitleDetail = false"
          v-if="showTitleDetail && props.data._id"></mdi-icon>
        <span v-if="showTitleDetail">{{ props.data._id }}</span>
      </div>
    </template>
    <CdForm ref="dialogFormRef" :data="props.data" :config="props.config" :expertMode="isExpert"></CdForm>
    <template v-slot:footer>
      <span class="dialog-footer">
        <div style="float: left; margin-left: 100px" class="dialog-expert" v-if="!hideExpertMode">
          专家模式
          <el-switch v-model="isExpert"></el-switch>
        </div>

        <el-button @click="close" size="small"> 取消</el-button>
        <el-button type="primary" @click="tryConfirm" size="small"> 确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { ContainerProps } from './support'

import CdForm from './Form.vue'
//
interface DialogProps extends ContainerProps {
  callback: Function
  callbackClosed: Function
}
//
const props = defineProps<DialogProps>()
const emit = defineEmits<{
  dataUpdate: [data: any]
}>()
//
const model = defineModel({ type: Boolean })

//data
const isExpert = ref(false) //与属性expertMode冲突所以改名
const showTitleDetail = ref(false) //是否显示标题中其他部分
//
const dialogAttr = computed(() => {
  const defaultAttr = {
    draggable: true,
    width: '60%',
    appendToBody: true,
    closeOnClickModal: false
  }

  return { ...defaultAttr, ...(props.config.props || {}) }
})
//computed

//是否显示专家模式按钮,缺省是false
const hideExpertMode = computed(() => {
  let hideExpertMode = props.config.hideExpertMode

  if (hideExpertMode == undefined) {
    //没有设置认为是 true
    return false
  }
  return !!hideExpertMode
})

//methods
function close() {
  model.value = false
}

function handleClose() {
  //处理关闭
  if (props.callbackClosed && typeof props.callbackClosed === 'function') {
    props.callbackClosed()
  }
}
//
const dialogFormRef = ref()
//点击确定后调用,先验证是否合法.如果合法则关闭窗口抛出事件
function tryConfirm() {
  dialogFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    if (props.callback && typeof props.callback === 'function') {
      props.callback(props.data)
    } else {
      emit('dataUpdate', props.data)
    }
    //
    close()
  })
}
//
function show() {
  model.value = true
}
defineExpose({ show })
</script>
