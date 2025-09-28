<template>
    <div class="task-card">
        <div class="task-header">
            <div class="task-title">
                <h3>{{ task.taskName }}</h3>
            </div>
            <el-button type="danger" size="small" @click="handleCancel">
                取消任务
            </el-button>
        </div>

        <div class="task-content">
            <div class="task-field">
                <label>任务请求:</label>
                <div class="field-value">{{ getTaskRequest(task.request) }}</div>
            </div>

            <div class="task-field">
                <label>定时详情:</label>
                <div class="field-value">{{ getSchedulerDescription(task.scheduler) }}</div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { schedulerAPI } from '../../../api/scheduler'

interface Props {
    task: any
}

const props = defineProps<Props>()

const emit = defineEmits<{
    taskCancelled: []
}>()

// 获取任务请求显示内容
const getTaskRequest = (request: string) => {
    try {
        const requestObj = JSON.parse(request)
        const keys = Object.keys(requestObj)
        if (keys.length === 1 && keys[0] === 'query') {
            return requestObj.query
        }
        return request
    } catch {
        return request
    }
}

// 获取定时详情描述
const getSchedulerDescription = (scheduler: any) => {
    const { mode } = scheduler

    switch (mode) {
        case 'FIXED':
            return `执行一次，运行时间：${scheduler.fixedTime}`

        case 'SIMPLE':
            const startTime = scheduler.startTime || '立即开始'
            const endTime = scheduler.endTime || '无限制'
            const interval = scheduler.repeatInterval
            const unit = getUnitText(scheduler.repeatUnit)
            const count = scheduler.repeatCount === -1 ? '无限制' : `${scheduler.repeatCount}`

            return `周期执行，开始时间：${startTime}，结束时间：${endTime}，周期：${interval}${unit}，最多运行次数：${count}`

        case 'CRON':
            return `CRON表达式：${scheduler.cronExpression}`

        default:
            return JSON.stringify(scheduler)
    }
}

// 获取单位文本
const getUnitText = (unit: string) => {
    const unitMap: Record<string, string> = {
        'hour': '小时',
        'minute': '分钟',
        'second': '秒',
        'milliSecond': '毫秒'
    }
    return unitMap[unit] || unit
}

// 取消定时任务
const handleCancel = async () => {
    try {
        await ElMessageBox.confirm(
            `确定要取消任务"${props.task.taskName}"吗？`,
            '确认取消',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await schedulerAPI.cancelJob({
            jobName: props.task.jobName,
            jobGroup: props.task.jobGroup
        })

        ElMessage.success('任务取消成功')
        emit('taskCancelled')
    } catch (error: any) {
        if (error !== 'cancel') {
            console.error('取消任务失败:', error)
            ElMessage.error('取消任务失败')
        }
    }
}
</script>

<style scoped>
.task-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 20px;
    background-color: #fff;
    transition: all 0.3s ease;
}

.task-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
}

.task-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
}

.task-title h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.task-content {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.task-field {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.task-field label {
    font-size: 12px;
    color: #909399;
    font-weight: 500;
}

.field-value {
    font-size: 14px;
    color: #303133;
    word-break: break-all;
    line-height: 1.5;
    padding: 8px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .task-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
    }

    .task-content {
        gap: 16px;
    }
}
</style>
