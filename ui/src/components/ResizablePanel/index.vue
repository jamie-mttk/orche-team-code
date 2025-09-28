<template>
    <div class="resizable-panel" ref="containerRef">
        <!-- 左侧面板 -->
        <div class="left-panel" :style="{ width: leftWidth + '%' }">
            <slot name="left"></slot>
        </div>

        <!-- 可拖动的分隔条 -->
        <div class="resize-handle" @mousedown="startResize" :class="{ 'resizing': isResizing }"></div>

        <!-- 右侧面板 -->
        <div class="right-panel" :style="{ width: (100 - leftWidth) + '%' }">
            <slot name="right"></slot>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted, watch, onMounted } from 'vue'

// Props
interface Props {
    leftWidth?: number
    minLeftWidth?: number
    maxLeftWidth?: number
    identifier?: string
}

const props = withDefaults(defineProps<Props>(), {
    leftWidth: 30,
    minLeftWidth: 20,
    maxLeftWidth: 80,
    identifier: 'default'
})

// Emits
interface Emits {
    (e: 'update:leftWidth', value: number): void
}

const emit = defineEmits<Emits>()

// 响应式数据
const containerRef = ref<HTMLElement>()
const isResizing = ref(false)
const leftWidth = ref(props.leftWidth)

// 存储键名
const storageKey = `resizablePanel_${props.identifier}`

// 从本地存储加载宽度
const loadWidthFromStorage = () => {
    try {
        const storedWidth = localStorage.getItem(storageKey)
        if (storedWidth) {
            const parsedWidth = parseInt(storedWidth, 10)
            if (!isNaN(parsedWidth) && parsedWidth >= props.minLeftWidth && parsedWidth <= props.maxLeftWidth) {
                leftWidth.value = parsedWidth
                emit('update:leftWidth', leftWidth.value)
            }
        }
    } catch (error) {
        console.warn('Failed to load width from localStorage:', error)
    }
}

// 保存宽度到本地存储
const saveWidthToStorage = (width: number) => {
    try {
        localStorage.setItem(storageKey, width.toString())
    } catch (error) {
        console.warn('Failed to save width to localStorage:', error)
    }
}

// 监听props变化
watch(() => props.leftWidth, (newValue) => {
    leftWidth.value = newValue
})

// 监听leftWidth变化并保存到本地存储
watch(leftWidth, (newValue) => {
    saveWidthToStorage(newValue)
})

// 组件挂载时加载存储的宽度
onMounted(() => {
    loadWidthFromStorage()
})

// 开始拖拽调整大小
const startResize = (e: MouseEvent) => {
    e.preventDefault()
    isResizing.value = true

    document.addEventListener('mousemove', handleResize)
    document.addEventListener('mouseup', stopResize)
    document.body.style.cursor = 'col-resize'
    document.body.style.userSelect = 'none'
}

// 处理拖拽调整大小
const handleResize = (e: MouseEvent) => {
    if (!isResizing.value || !containerRef.value) return

    const containerRect = containerRef.value.getBoundingClientRect()
    const mouseX = e.clientX - containerRect.left
    const containerWidth = containerRect.width

    // 计算新的宽度百分比
    const newLeftWidth = (mouseX / containerWidth) * 100

    // 限制最小和最大宽度
    if (newLeftWidth >= props.minLeftWidth && newLeftWidth <= props.maxLeftWidth) {
        leftWidth.value = Math.round(newLeftWidth)
        emit('update:leftWidth', leftWidth.value)
    }
}

// 停止拖拽调整大小
const stopResize = () => {
    isResizing.value = false

    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResize)
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
}

// 组件卸载时清理事件监听器
onUnmounted(() => {
    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResize)
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
})
</script>

<style scoped>
.resizable-panel {
    display: flex;
    height: 100%;
    width: 100%;
}

.left-panel {
    height: 100%;
    overflow: hidden;
}

.right-panel {
    height: 100%;
    overflow: hidden;
}

.resize-handle {
    width: 4px;
    background-color: #e4e7ed;
    cursor: col-resize;
    position: relative;
    transition: background-color 0.2s;
    flex-shrink: 0;
}

.resize-handle:hover {
    background-color: #409eff;
}

.resize-handle.resizing {
    background-color: #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .resizable-panel {
        flex-direction: column;
    }

    .left-panel,
    .right-panel {
        width: 100% !important;
        height: 50%;
    }

    .resize-handle {
        width: 100%;
        height: 4px;
        cursor: row-resize;
    }
}
</style>
