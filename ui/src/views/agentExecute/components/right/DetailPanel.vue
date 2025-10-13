<template>
    <div class="detail-panel">
        <div class="detail-header">
            <h3>{{ selectedNodeTitle }}</h3>
            <el-tag v-if="selectedNodeType" :type="detailStatusType" size="small">
                {{ detailStatusText }}
            </el-tag>
        </div>
        <div class="detail-container">
            <component v-if="selectedNodeData && selectedComponent" :is="selectedComponent" :data="selectedNodeData"
                :agentRuntime="agentRuntime" class="detail-component" />
            <div v-else class="no-selection">
                <el-empty description="请选择左侧节点查看详细信息" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { AgentRuntime } from '../../support/agentRuntimeSupport';

// Props
interface Props {
    agentRuntime: AgentRuntime
}

const props = defineProps<Props>()
const selectedNode = computed(() => {
    return props.agentRuntime?.getSelectedNode()
})
// 计算属性
const selectedNodeData = computed(() => {
    const data = selectedNode?.value?.displayResult?.getData() || null

    return data
})

const selectedNodeType = computed(() => {
    const type = selectedNode?.value?.label || ''

    return type
})

const selectedNodeTitle = computed(() => {
    const title = selectedNode?.value?.displayResult?.getTitle() || '请选择节点'

    return title
})

// 选择组件
const selectedComponent = computed(() => {
    if (!selectedNode?.value?.displayResult) {

        return null
    }

    const component = selectedNode?.value?.displayResult.getComp()

    return component
})

// 获取详细内容状态类型
const detailStatusType = computed(() => {
    if (!selectedNode?.value) return 'info'

    // 使用节点的实际状态
    if (selectedNode?.value?.status === 'success') return 'success'
    if (selectedNode?.value?.status === 'processing') return 'warning'
    if (selectedNode?.value?.status === 'failed') return 'danger'

    return 'info'
})

// 获取详细内容状态文本
const detailStatusText = computed(() => {
    if (!selectedNode?.value) return '未知'

    // 使用节点的实际状态
    if (selectedNode?.value?.status === 'success') return '已完成'
    if (selectedNode?.value?.status === 'processing') return '进行中'
    if (selectedNode?.value?.status === 'failed') return '失败'

    return '处理中'
})
</script>

<style scoped>
.detail-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 60px;
    /* 固定高度 */
    padding: 0 15px;
    /* 只保留左右padding */
    border-bottom: 1px solid #e4e7ed;
    flex-shrink: 0;
    /* 防止被压缩 */
}

.detail-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.detail-container {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
    min-height: 0;
    box-sizing: border-box;
    height: 0;
    /* 强制flex子元素计算高度 */
    max-height: calc(100vh - 120px);
    /* 设置最大高度限制 */
}

.detail-component {
    padding: 10px 15px;
    box-sizing: border-box;
}

.no-selection {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
