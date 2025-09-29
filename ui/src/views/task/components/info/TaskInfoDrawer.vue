<template>
    <el-drawer v-model="visible" :title="drawerTitle" direction="rtl" size="60%" :append-to-body="true"
        :before-close="handleClose">
        <div class="task-info-content">
            <!-- 任务基本信息 -->
            <div class="info-section">
                <h3 class="section-title">
                    <el-icon>
                        <InfoFilled />
                    </el-icon>
                    基本信息
                </h3>
                <div class="info-grid">
                    <div class="info-item">
                        <span class="label">任务名称：</span>
                        <span class="value">{{ task?.name || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">会话ID：</span>
                        <span class="value">{{ task?.sessionId || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">状态：</span>
                        <StatusDisplay :status="task?.status || 'waiting'" />
                    </div>
                    <TimeDisplay label="开始时间" :time="task?.startTime" />
                    <TimeDisplay label="结束时间" :time="task?.finishTime" />
                </div>
            </div>

            <!-- 智能体信息 -->
            <div class="info-section">
                <h3 class="section-title">
                    <el-icon>
                        <User />
                    </el-icon>
                    智能体信息
                </h3>
                <div v-if="agentRuntime?.agent?.value" class="agent-info">
                    <div class="info-item">
                        <span class="label">名称：</span>
                        <span class="value">{{ agentRuntime.agent.value.name || '-' }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">描述：</span>
                        <span class="value">{{ agentRuntime.agent.value.description || '-' }}</span>
                    </div>
                </div>
                <div v-else class="no-data">
                    <el-empty description="暂无智能体信息" />
                </div>
            </div>

            <!-- 任务信息 -->
            <div class="info-section">
                <h3 class="section-title">
                    <el-icon>
                        <InfoFilled />
                    </el-icon>
                    任务信息
                </h3>
                <div v-if="agentRuntime?.agentInputConfig?.value" class="task-form">
                    <CdForm ref="inputFormRef" :config="agentRuntime.agentInputConfig.value" :data="formData"
                        :disabledParent="true" style="width: 100%;" />
                </div>
                <div v-else class="no-data">
                    <el-empty description="暂无任务信息" />
                </div>
            </div>

            <!-- 文件列表 -->
            <div class="info-section">
                <h3 class="section-title">
                    <el-icon>
                        <Document />
                    </el-icon>
                    文件列表
                </h3>
                <div v-if="task?.files && task.files.length > 0" class="files-container">
                    <FileDisplay :files="task.files" :session-id="task.sessionId" />
                </div>
                <div v-else class="no-data">
                    <el-empty description="暂无文件" />
                </div>
            </div>
        </div>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { InfoFilled, User, Document } from '@element-plus/icons-vue'
import FileDisplay from '@/views/agentExecute/support/components/fileDisplay/index.vue'
import StatusDisplay from '@/components/statusDisplay/index.vue'
import TimeDisplay from '@/components/timeDisplay/index.vue'
import { AgentRuntime } from '@/views/agentExecute/support/agentRuntimeSupport'
import CdForm from '@/components/ConfigDyna/Form.vue'
const visible = ref(false)
let agentRuntime: any = undefined
const formData = ref<any>(null)
const task = ref<any>(null)

const drawerTitle = computed(() => task.value?.name || '任务详情')



const show = async (taskParam: any) => {
    task.value = taskParam
    visible.value = true
    agentRuntime = new AgentRuntime(taskParam.agentId)
    formData.value = taskParam.request ? JSON.parse(taskParam.request) : {}
}

const handleClose = () => {
    visible.value = false
    agentRuntime = undefined
    formData.value = null
    task.value = null
}




defineExpose({
    show
})
</script>

<style scoped>
.task-info-content {
    padding: 0 20px;
}

.info-section {
    margin-bottom: 32px;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid var(--el-border-color-lighter);
}

.info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.info-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
}

.info-item .label {
    font-weight: 500;
    color: var(--el-text-color-regular);
    min-width: 80px;
    flex-shrink: 0;
}

.info-item .value {
    color: var(--el-text-color-primary);
    word-break: break-all;
}

.request-content {
    background: var(--el-fill-color-light);
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    padding: 12px;
    margin-top: 8px;
}

.request-content pre {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-word;
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 13px;
    line-height: 1.5;
    color: var(--el-text-color-primary);
}

.agent-info {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.task-form {
    margin-top: 8px;
}


.no-data {
    text-align: center;
    padding: 20px 0;
}

.files-container {
    margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .info-grid {
        grid-template-columns: 1fr;
    }

    .info-item {
        flex-direction: column;
        align-items: flex-start;
    }

    .info-item .label {
        min-width: auto;
    }
}
</style>
