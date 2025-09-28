<template>
  <el-tooltip effect="light" :disabled="!props.tooltip" :raw-content="true" :content="props.tooltip" :show-after="1000"
    placement="bottom">
    <i class="lc-icon" :style="styleObject" aria-hidden="true" v-bind="$attrs" @click="handleClick">
      <svg class="lc-icon_svg" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" role="img" aria-hidden="true">
        <path :d="(mdi as any)[toHump(props.name || '')]"></path>
      </svg>
    </i>
  </el-tooltip>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import * as mdi from '@mdi/js'
import { toHump } from './iconUtil'

const predefinedSize = {
  'x-small': '1em',
  small: '1.25em',
  medium: '1.5em',
  large: '1.75em',
  'x-large': '2em'
}
const props = defineProps<{
  name?: string
  color?: string
  size?: string
  tooltip?: string
}>()

const emit = defineEmits<{
  (e: 'click', data: Object): void
}>()

const styleObject = reactive({
  color: props.color || '',
  fontSize: (predefinedSize as any)[props.size || 'medium'] || props.size
})

//el-tooltip will override the click event,so we raise event here
function handleClick(event: Event) {
  emit('click', event)
  // event.preventDefault();
  //   event.stopPropagation();
}
</script>

<style scoped>
.lc-icon {
  --v-icon-size-multiplier: 1;
  align-items: center;
  display: inline-flex;
  font-feature-settings: 'liga';
  height: 1em;
  justify-content: center;
  letter-spacing: normal;
  line-height: 1;
  position: relative;
  text-indent: 0;
  user-select: none;
  vertical-align: middle;
  width: 1em;
  cursor: pointer;
}

.lc-icon_svg {
  fill: currentColor;
  width: 100%;
  height: 100%;
}
</style>
