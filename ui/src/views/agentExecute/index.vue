<template>
    <div class="agent-execute-container">
        <!-- 页头 -->
        <div class="page-header">
            <el-page-header @back="handleBack" title="返回">
                <template #content>
                    <span class="page-title"> {{ agentRuntime.agent.value?.name }}</span>
                </template>
            </el-page-header>
        </div>

        <!-- 主要内容区域 -->
        <div class="main-content">
            <ResizablePanel identifier="agentExecuteMain" :min-left-width="20" :max-left-width="80" :left-width="30">
                <template #left>
                    <AgentRequestInput :agent-runtime="agentRuntime" />
                </template>

                <template #right>
                    <ResultDisplay :agent-runtime="agentRuntime" />
                </template>
            </ResizablePanel>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import AgentRequestInput from './components/left/AgentRequestInput.vue'
import ResultDisplay from './components/right/ResultDisplay.vue'
import ResizablePanel from '@/components/ResizablePanel/index.vue'
import { AgentRuntime } from './support/agentRuntimeSupport'


// 路由实例
const route = useRoute()
const router = useRouter()



// 创建AgentRuntime实例
const agentRuntime: AgentRuntime = new AgentRuntime(
    route.params.agentId as string

)


// 页面加载时获取智能体数据
// onMounted(() => {
//     loadAgent()
// })

// 返回处理
const handleBack = () => {
    router.push('/agent')
}



</script>

<style scoped>
.agent-execute-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: var(--el-fill-color-extra-light);
}

.page-header {
    background: white;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-light);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    border-radius: 8px;
}

.page-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.main-content {
    flex: 1;
    padding: 10px 0;
    overflow: hidden;
}

.empty-result {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
    border-radius: 8px;
    border: 1px solid var(--el-border-color-light);
}

/* 响应式布局 */
@media (max-width: 768px) {
    .page-header {
        padding: 12px 16px;
    }

    .main-content {
        flex-direction: column;
        padding: 16px;
    }

    .left-panel,
    .right-panel {
        width: 100% !important;
        height: 50vh;
    }

    .resize-handle {
        width: 100%;
        height: 4px;
        cursor: row-resize;
        margin: 8px 0;
    }

    .resize-handle::before {
        top: -4px;
        left: 0;
        right: 0;
        bottom: -4px;
    }
}
</style>