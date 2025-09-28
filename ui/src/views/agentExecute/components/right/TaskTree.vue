<template>
    <div class="task-tree-panel">
        <div class="tree-header">
            <h3>任务执行流程</h3>
            <div class="status-icon">
                <span class="execution-time" :class="getOverallTimeClass()">
                    {{ getOverallExecutionTime() }}
                </span>
            </div>
        </div>
        <div class="tree-container">
            <div v-if="props.agentRuntime?.treeNodes?.value?.length === 0" class="no-tree-data">
                <el-empty description="等待任务开始..." />
            </div>

            <el-tree ref="taskTreeRef" v-else :data="props.agentRuntime?.treeNodes?.value" :props="treeProps"
                default-expand-all :expand-on-click-node="false" :highlight-current="false"
                :current-node-key="props.agentRuntime?.selectedNode?.value?.id" @node-click="handleNodeClick"
                class="task-tree" node-key="id">
                <template #default="{ data }">
                    <div class="tree-node">
                        <div class="node-content">
                            <span class="node-label" :title="data.label">{{ data.label }}</span>
                            <div class="node-execution-time" :class="getTimeClass(data.status)">
                                {{ getExecutionTime(data) }}
                            </div>
                        </div>
                    </div>
                </template>
            </el-tree>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import moment from 'moment'
import { AgentRuntime, type TreeNode } from '../../support/agentRuntimeSupport';
import { formatDuaration } from '@/utils/tools';
// Props
interface Props {
    agentRuntime: AgentRuntime
}


const props = defineProps<Props>()

// 树形结构配置
const treeProps = {
    children: 'children',
    label: 'label'
}
const taskTreeRef = ref()

// 当前时间，用于计算执行时间
const currentTime = ref(Date.now())

// 定时器引用
let timer: number | null = null

// 启动定时器，每秒更新一次
onMounted(() => {
    timer = setInterval(() => {
        // 只有在还有任务在执行时才更新当前时间
        if (hasProcessing.value) {
            currentTime.value = Date.now()
        }
    }, 1000)
})

// 清理定时器
onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
    }
})

// 是否有节点仍然在执行
const hasProcessing = computed(() => {
    if (props.agentRuntime?.treeNodes?.value?.length > 0) {
        return props.agentRuntime?.treeNodes?.value?.some((node: TreeNode) =>
            node.status === 'processing'
        )
    }
    return false
})



// 获取节点执行时间
const getExecutionTime = (node: TreeNode): string => {
    // 为每个节点设置默认开始时间（如果没有的话）
    if (!(node as any).startTime) {
        (node as any).startTime = moment().valueOf()
    }

    const startTime = node.startTime || moment().valueOf()
    const endTime = node.endTime || currentTime.value
    // let endTime = currentTime.value


    const duration = endTime - startTime
    return formatDuaration(duration)
}

// 获取整体执行时间
const getOverallExecutionTime = (): string => {
    if (!props.agentRuntime?.treeNodes?.value || props.agentRuntime?.treeNodes?.value?.length === 0) return '0s'

    // 为没有开始时间的节点设置默认时间
    const nodesWithTime = props.agentRuntime?.treeNodes?.value

    // 使用最早的任务开始时间
    const startTime = Math.min(...nodesWithTime
        .map(node => (node as any).startTime)
    )

    // 如果所有任务都完成了，使用最后一个任务的结束时间
    if (!hasProcessing.value) {
        // 找到所有已完成的任务
        const completedNodes = nodesWithTime.filter(node =>
            node.status === 'success' || node.status === 'failed'
        )

        if (completedNodes.length > 0) {
            // 为没有结束时间的节点设置结束时间
            const nodesWithEndTime = completedNodes

            const endTime = Math.max(...nodesWithEndTime
                .map(node => (node as any).endTime)
            )

            const duration = endTime - startTime
            return formatDuaration(duration)
        }

        // 如果没有找到已完成的任务，使用当前时间
        const duration = currentTime.value - startTime
        return formatDuaration(duration)
    }

    // 如果还有任务在执行，使用当前时间
    const duration = currentTime.value - startTime
    return formatDuaration(duration)
}

// 根据状态获取时间显示的CSS类
const getTimeClass = (status: string): string => {
    switch (status) {
        case 'success':
            return 'success-time'
        case 'failed':
            return 'failed-time'
        case 'processing':
            return 'processing-time'
        default:
            return 'waiting-time'
    }
}

// 获取整体执行时间的CSS类
const getOverallTimeClass = (): string => {
    if (hasProcessing.value) {
        return 'processing-time'
    }
    return 'success-time'
}

// 监视 selectedNode.id 的变化，自动设置树的高亮节点
// watch(
//     () => props.agentRuntime?.selectedNode?.value?.id,
//     (newId) => {
//         if (newId && taskTreeRef.value) {
//             taskTreeRef.value.setCurrentKey(newId)
//         }
//     },
//     { immediate: true }
// )

// 处理节点点击
const handleNodeClick = (node: TreeNode) => {
    // emit('nodeClick', node)
    props.agentRuntime.selectedNode.value = node
}
</script>

<style scoped>
.task-tree-panel {
    padding-right: 10px;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    height: 100%;
    max-height: 100vh;
}

.tree-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #e4e7ed;
}

.tree-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.status-icon {
    display: flex;
    align-items: center;
}

.execution-time {
    font-size: 14px;
    font-weight: 600;
    font-family: 'Courier New', monospace;
}

.processing-time {
    color: #e6a23c;
}

.success-time {
    color: #67c23a;
}

.failed-time {
    color: #f56c6c;
    /* font-size: 14px;
    font-weight: 600; */
}

.waiting-time {
    color: #909399;
    font-size: 14px;
    font-weight: 600;
}

.tree-container {
    flex: 1;
    overflow-y: auto;
    padding: 10px 8px 10px 0;
    min-height: 0;
    max-height: calc(100vh - 120px);
}

.no-tree-data {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.task-tree {
    background: none;
}

.tree-node {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 2px 0;
}

.node-content {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    width: 100%;
    position: relative;
}

.node-label {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: left;
    margin-right: 90px;
}

.node-execution-time {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    font-size: 12px;
    font-weight: 600;
    padding: 4px 8px;
    border-radius: 4px;
    background-color: rgba(255, 255, 255, 0.95);
    font-family: 'Courier New', monospace;
    min-width: 60px;
    text-align: center;
    z-index: 10;
}
</style>
