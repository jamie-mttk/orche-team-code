<template>
  <CdBase :config="props.config" :data="props.data">
    <div class="editor-container">
      <el-alert :title="placeholder" type="primary" />
      <MttkWrapComp :config="wrapConfig" ref="monacoEditorRef" class="monaco-editor-wrapper"></MttkWrapComp>
      <div class="drag-bar" :style="dragBarStyle" @mousedown="startDrag"></div>
    </div>
  </CdBase>
</template>
<script setup lang="ts">
import { ref, computed, unref } from 'vue'
import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
import { buildMonacoEditorComp } from '@/components/util/monacoEditor'
import { MttkWrapComp } from 'mttk-vue-wrap'

const props = defineProps<BaseProps>()
const { modelValue, isDisabled, getPropSafe, placeholder } = useSupport(props)
//
const monacoEditorRef = ref(null)
//
const editorHeight = ref(parseInitHeight())
//

const wrapConfig = buildMonacoEditorComp(modelValue, {
  language: getPropSafe('language'),
  options: {
    readOnly: unref(isDisabled),
    lineNumbers: 'off',
    minimap: {
      enabled: false
    },
    scrollbar: {
      vertical: 'auto',        // 垂直滚动条：'auto' | 'visible' | 'hidden'
      horizontal: 'hidden',      // 水平滚动条：'auto' | 'visible' | 'hidden'
      verticalScrollbarSize: 4, // 垂直滚动条宽度（像素）
      horizontalScrollbarSize: 4, // 水平滚动条高度（像素）

    },
  },
  style: computed(() => {
    return { height: editorHeight.value + 'px' }
  })
})

//init height - 如果是字符串需要转换成int
function parseInitHeight() {
  let h = getPropSafe('height')
  if (!h) {
    return 100
  }
  if (typeof h == 'number') {
    return h
  } else {
    return parseInt(h)
  }
}
//  根据高度设置拖拽块高度,兼顾美观和易用
const dragBarStyle = computed(() => {
  return { height: editorHeight.value > 100 ? '4px' : '2px' }
})
//后面实现了拖动修改高度
let startY = 0
let startHeight = 0

function startDrag(e) {
  startY = e.clientY
  startHeight = editorHeight.value
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

function onDrag(e) {
  const deltaY = e.clientY - startY
  editorHeight.value = startHeight + deltaY
  // 限制最小高度
  if (editorHeight.value < 32) editorHeight.value = 32
}

function stopDrag() {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

function getEditorRef() {
  return monacoEditorRef
}
//
defineExpose({ getEditorRef })
</script>

<style lang="scss">
.editor-container {
  position: relative;
  width: 100%;
  height: auto;
  min-height: 32px;

  /* 修改背景色为白色,和其他界面保持一致 */

  .monaco-editor-background {
    background-color: #ffffff !important;
  }
}

.monaco-editor-wrapper {
  padding: 20px;
  border: 1px solid var(--el-border-color);
  border-radius: var(--el-border-radius-base);
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  background-color: var(--el-fill-color-blank);

  &:hover {
    border-color: var(--el-border-color-hover);
  }

  &:focus-within {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
  }

  // 错误状态样式
  .el-form-item.is-error & {
    border-color: var(--el-color-danger);

    &:focus-within {
      border-color: var(--el-color-danger);
      box-shadow: 0 0 0 2px var(--el-color-danger-light-8);
    }
  }
}

.drag-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;

  background-color: #ddd;
  cursor: row-resize;
  z-index: 10;
}
</style>
