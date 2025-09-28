<template>
    <div class="main-content">
        <div class="content-layout">
            <!-- 左侧任务树 -->
            <div class="task-tree-container">
                <TaskTree :agentRuntime="agentRuntime" />
            </div>

            <!-- 右侧详细内容 -->
            <div class="detail-panel-container">
                <DetailPanel :agentRuntime="agentRuntime" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import TaskTree from './TaskTree.vue'
import DetailPanel from './DetailPanel.vue'

import type { AgentRuntime } from '../../support/agentRuntimeSupport'

// Props
interface Props {
    agentRuntime: AgentRuntime
}

defineProps<Props>()
</script>

<style scoped>
.main-content {
    flex: 1;
    overflow: hidden;
    padding: 0;
}

.content-layout {
    height: 100%;
    display: flex;
    gap: 16px;
}

.task-tree-container {
    width: 30%;
    flex-shrink: 0;
    height: 100%;
}

.detail-panel-container {
    flex: 1;
    height: 100%;
    min-width: 0;
}
</style>
