<template>
    <div class="status-display">
        <el-tag :type="getStatusTagType(props.status)" size="small">
            {{ getStatusText(props.status) }}
        </el-tag>
    </div>
</template>

<script setup lang="ts">

// 定义状态类型
export type ExecutionStatus = 'waiting' | 'running' | 'success' | 'failed'

// Props
interface Props {
    status: ExecutionStatus
}

const props = defineProps<Props>()


// 获取状态标签类型
const getStatusTagType = (status: string) => {
    const typeMap = {
        waiting: 'info',
        running: 'warning',
        success: 'success',
        failed: 'danger'
    } as const
    return typeMap[status as keyof typeof typeMap] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
    const textMap = {
        waiting: '等待处理',
        running: '执行中',
        success: '已完成',
        failed: '执行失败'
    } as const
    return textMap[status as keyof typeof textMap] || '未知'
}
</script>

<style scoped>
.status-display {
    display: inline-block;
}
</style>
