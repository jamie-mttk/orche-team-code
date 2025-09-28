<template>
    <div class="result-display">
        <el-card class="main-card">
            <template #header>
                <div class="main-header">
                    <div class="query-info">
                        <span class="query-text">执行结果</span>
                    </div>

                </div>
            </template>

            <ResultDisplayReal :agentRuntime="agentRuntime" />
        </el-card>
    </div>
</template>

<script setup lang="ts">
import ResultDisplayReal from './ResultDisplayReal.vue'

import type { AgentRuntime } from '../../support/agentRuntimeSupport'

// Props
interface Props {
    agentRuntime: AgentRuntime
}

defineProps<Props>()
</script>

<style scoped>
.result-display {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: var(--el-fill-color-extra-light);
}

.main-card {
    height: 100%;
    display: flex;
    flex-direction: column;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.main-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0;
}

.query-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.query-text {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.header-actions {
    display: flex;
    gap: 8px;
}
</style>