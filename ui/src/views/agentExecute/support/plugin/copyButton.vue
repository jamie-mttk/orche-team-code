<template>
    <el-button ref="buttonRef" text :type="isHovered ? 'primary' : ''" size="small" @click.stop="copyToClipboard"
        class="copy-button" title="复制内容">
        <icon name="mdiContentCopy" size="small" />
    </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useElementHover } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'

interface Props {
    content: string
}

const props = defineProps<Props>()

// 按钮元素引用
const buttonRef = ref()

// 使用 VueUse 的 useElementHover 检测鼠标悬停状态
const isHovered = useElementHover(buttonRef)

// 拷贝到剪贴板
const copyToClipboard = async () => {
    try {
        await navigator.clipboard.writeText(props.content)
        ElMessage.success('内容已复制到剪贴板')
    } catch (err) {
        console.error('复制失败:', err)
        ElMessage.error('复制失败')
    }
}
</script>

<style scoped>
.copy-button {
    width: 24px;
    height: 24px;
    padding: 0;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 24px;
    min-height: 24px;

    transition: color 0.3s;
}

.copy-button :deep(.mdi-icon) {
    width: 14px;
    height: 14px;
}
</style>
