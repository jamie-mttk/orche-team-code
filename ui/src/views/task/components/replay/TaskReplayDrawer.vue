<template>
    <el-drawer v-model="visible" :title="drawerTitle" direction="rtl" size="80%" :close-on-click-modal="false"
        :close-on-press-escape="false" :show-close="true" :append-to-body="true">
        <template #header>
            <div class="drawer-header">
                <div class="header-content">
                    <Icon name="mdiPlayCircle" size="medium" class="header-icon" />
                    <span class="header-title">{{ drawerTitle }}</span>
                </div>
            </div>
        </template>

        <div class="drawer-content">


            <div class="replay-container">
                <!-- 这里使用 ResultDisplay 组件 -->
                <ResultDisplayReal v-if="agentRuntime" :agentRuntime="agentRuntime as unknown as AgentRuntime" />
                <div v-else class="loading-placeholder">
                    <el-empty description="正在加载回放数据..." />
                </div>
            </div>
        </div>


    </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import ResultDisplayReal from '@/views/agentExecute/components/right/ResultDisplayReal.vue'
import { AgentRuntime } from '@/views/agentExecute/support/agentRuntimeSupport'

// 响应式数据
const visible = ref(false)

// const agentRuntime = ref<AgentRuntime | null>(null)
let agentRuntime: any = undefined
//当前任务
const currentTask = ref<any>(null)

// 
const drawerTitle = computed(() => {
    return currentTask.value ? `回放任务: ${currentTask.value.name}` : '任务回放'
})
//



const show = async (task: any) => {
    if (!task) return

    currentTask.value = task
    visible.value = true
    await loadReplayData()
}

const loadReplayData = async () => {
    if (!currentTask.value) return



    // 使用工厂函数创建AgentRuntime实例，确保初始化完成
    // agentRuntime.value = await createAgentRuntime(currentTask.value.agentId)
    agentRuntime = new AgentRuntime(currentTask.value.agentId)
    if (!agentRuntime) return
    //
    agentRuntime.replay(currentTask.value.sessionId)

}


// 暴露方法给外部调用
defineExpose({
    show
})
</script>

<style scoped>
.drawer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 0;
}

.header-content {
    display: flex;
    align-items: center;
    gap: 8px;
}

.header-icon {
    color: var(--el-color-primary);
}

.header-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.drawer-content {
    height: 100%;
    display: flex;
    flex-direction: column;
}


.replay-container {
    height: 100%;
    display: flex;
    flex-direction: column;
}

/* 确保drawer内容区域占满高度 */
:deep(.el-drawer__body) {
    padding: 0;
    height: calc(100% - 60px);
    overflow: hidden;
}

/* 自定义drawer头部样式 */
:deep(.el-drawer__header) {
    margin-bottom: 0;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
}
</style>