<template>
    <div class="task-item" :class="getTaskStatusClass(task.status)">
        <div class="task-main">
            <div class="task-header">
                <div class="task-title">
                    {{ task.name }}
                    <el-tag v-if="task.mode === 'SCHEDULER'" type="info" size="small" class="mode-tag">
                        定时任务
                    </el-tag>
                </div>
                <div class="task-status">
                    <el-tag :type="getStatusTagType(task.status)" size="small">
                        {{ getStatusText(task.status) }}
                    </el-tag>
                </div>
            </div>
            <div class="task-request">
                <div class="request-text" v-html="displayRequest"></div>
            </div>
            <div class="task-meta">
                <div class="meta-left">
                    <div class="task-time">
                        <Icon name="mdiClock" size="small" />
                        <TimeDisplay label="" :time="task.startTime" />
                    </div>
                    <div v-if="taskDuration" class="task-duration">
                        <Icon name="mdiTimer" size="small" />
                        {{ taskDuration }}
                    </div>
                </div>
                <div class="task-actions">
                    <el-button size="small" type="primary" @click="handleView">
                        查看
                    </el-button>
                    <el-button size="small" type="default" @click="handleReplay">
                        回放
                    </el-button>
                    <el-button v-if="task.status === 'running'" size="small" type="danger" @click="handleStop">
                        停止
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 回放drawer -->
        <TaskReplayDrawer ref="taskReplayDrawerRef" />

        <!-- 任务详情drawer -->
        <TaskInfoDrawer ref="taskInfoDrawerRef" />
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import moment from 'moment'
import Icon from '@/components/mdiIicon/index.vue'
import TimeDisplay from '@/components/timeDisplay/index.vue'
import TaskReplayDrawer from './replay/TaskReplayDrawer.vue'
import TaskInfoDrawer from './info/TaskInfoDrawer.vue'
import { stopTaskExecution } from '@/views/agentExecute/components/left/chatUtil'
import { formatFileSize } from '@/utils/tools'

// Props
interface Props {
    task: any
}

const props = defineProps<Props>()

// 响应式数据
const taskReplayDrawerRef = ref<InstanceType<typeof TaskReplayDrawer> | null>(null)
const taskInfoDrawerRef = ref<InstanceType<typeof TaskInfoDrawer> | null>(null)



// 计算显示请求内容
const displayRequest = computed(() => {
    const request = props.task?.request

    if (!request) {
        return ''
    }

    try {
        // 尝试解析为JSON
        const parsed = JSON.parse(request)

        // 检查是否为对象
        if (typeof parsed === 'object' && parsed !== null && !Array.isArray(parsed)) {
            const keys = Object.keys(parsed)

            // 必须包含query
            if (!keys.includes('query')) {
                return request
            }

            // 除了query外，不包含其他不以_开头的key
            const nonUnderscoreKeys = keys.filter(key => key !== 'query' && !key.startsWith('_'))

            // 如果没有其他非下划线开头的key，返回query内容
            if (nonUnderscoreKeys.length === 0) {
                let html = `<div>${parsed.query}</div>`



                for (const key of keys) {
                    if (!key.startsWith('_')) {
                        continue;
                    }
                    const value = parsed[key]

                    if (!Array.isArray(value)) {
                        break;
                    }

                    html += '<div style="margin-top: 8px;"><strong>文件列表:</strong></div>'
                    html += '<ul style="margin: 4px 0; padding-left: 20px;">'

                    for (const file of value) {
                        if (typeof file === 'object' && file !== null && 'id' in file && 'name' in file) {
                            const fileName = file.name || '未知文件'
                            const size = file.size ? formatFileSize(file.size) : '未知大小'
                            const description = file.description ? ` - ${file.description}` : ''

                            html += `<li style="margin: 2px 0;">${fileName} (${size})${description}</li>`
                        }
                    }

                    html += '</ul>'


                }

                return html
            }
        }

        // 如果不满足条件，返回原始request
        return request
    } catch (error) {
        // 如果解析失败，返回原始request
        return request
    }
})

// 计算持续时间
const taskDuration = computed(() => {
    const { startTime, finishTime } = props.task
    // console.log('!!!!', startTime, finishTime)
    // 如果开始时间或结束时间为空，返回空字符串
    if (!startTime || !finishTime) {
        return ''
    }

    try {
        const start = moment(startTime, 'YYYY/MM/DD HH:mm:ss')
        const finish = moment(finishTime, 'YYYY/MM/DD HH:mm:ss')

        // 计算持续时间（秒）
        const duration = moment.duration(finish.diff(start))

        const hours = Math.floor(duration.asHours())
        const minutes = duration.minutes()
        const seconds = duration.seconds()

        // 构建时间字符串
        let result = ''
        if (hours > 0) {
            result += `${hours}h`
        }
        if (minutes > 0) {
            result += `${minutes}m`
        }
        if (seconds > 0) {
            result += `${seconds}s`
        }

        // 如果所有时间都为0，返回0s
        if (!result) {
            result = '0s'
        }

        return result
    } catch (error) {
        console.error('计算持续时间失败:', error)
        return ''
    }
})

// 方法
const handleView = async () => {
    await taskInfoDrawerRef.value?.show(props.task)
}

// const handleReplay = async () => {

//     let agentRuntime = new AgentRuntime(props.task.agentId)

//     //
//     agentRuntime.replay(props.task.sessionId)
// }
const handleReplay = async () => {
    await taskReplayDrawerRef.value?.show(props.task)
}

const handleStop = async () => {
    if (!props.task.sessionId) {
        ElMessage.error('任务会话ID不存在，无法停止')
        return
    }

    const success = await stopTaskExecution(props.task.sessionId)
    if (success) {
        ElMessage.success(`已发送停止请求: ${props.task.name}`)
    }
}

const getTaskStatusClass = (status: string) => {
    return `status-${status}`
}

const getStatusTagType = (status: string) => {
    const typeMap: Record<string, string> = {
        waiting: 'info',
        running: 'warning',
        success: 'success',
        failed: 'danger'
    }
    return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
    const textMap: Record<string, string> = {
        waiting: '等待处理',
        running: '执行中',
        success: '已完成',
        failed: '执行失败'
    }
    return textMap[status] || '未知'
}


</script>

<style scoped>
.task-item {
    padding: 20px 24px;
    background: white;
    border-radius: 12px;
    border: 1px solid var(--el-border-color-lighter);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.task-item:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
}

.status-waiting:hover {
    background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
}

.status-running:hover {
    background: linear-gradient(135deg, #ffe7ba 0%, #fff7e6 100%);
}

.status-success:hover {
    background: linear-gradient(135deg, #d9f7be 0%, #f6ffed 100%);
}

.status-failed:hover {
    background: linear-gradient(135deg, #ffccc7 0%, #fff2f0 100%);
}

.task-item:last-child {
    border-bottom: 1px solid var(--el-border-color-lighter);
}

.task-main {
    width: 100%;
}

.task-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.task-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    display: flex;
    align-items: center;
    gap: 8px;
}

.mode-tag {
    margin-left: 8px;
}

.task-request {
    margin-bottom: 12px;
}

.request-text {
    font-size: 14px;
    color: var(--el-text-color-regular);
    line-height: 1.5;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
}

.task-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: var(--el-text-color-secondary);
}

.meta-left {
    display: flex;
    gap: 20px;
    justify-content: flex-start;
}

.meta-left>div {
    display: flex;
    align-items: center;
    gap: 4px;
}

.task-actions {
    display: flex;
    gap: 8px;
}

/* 状态样式 */
.status-waiting {
    border-left: 4px solid #1890ff;
    background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
}

.status-running {
    border-left: 4px solid #ff9500;
    background: linear-gradient(135deg, #fff7e6 0%, #ffffff 100%);
}

.status-success {
    border-left: 4px solid #52c41a;
    background: linear-gradient(135deg, #f6ffed 0%, #ffffff 100%);
}

.status-failed {
    border-left: 4px solid #ff4d4f;
    background: linear-gradient(135deg, #fff2f0 0%, #ffffff 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .task-meta {
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
    }

    .meta-left {
        justify-content: flex-start;
    }

    .task-actions {
        justify-content: flex-end;
    }
}
</style>
