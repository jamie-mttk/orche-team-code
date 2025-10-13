<template>
    <div class="url-display">
        <el-card class="display-card">
            <template #header>
                <div class="card-header">
                    <span class="title">URL提取详情</span>
                    <el-tag :type="getStatusType()" size="small">{{ getStatusText() }}</el-tag>
                </div>
            </template>

            <div class="content-container">
                <div class="url-content">
                    <!-- URL信息 -->
                    <div class="url-info">
                        <el-descriptions :column="1" size="small" border :label-width="96">
                            <el-descriptions-item label="来自关键词">
                                {{ props.data.keyword }}
                            </el-descriptions-item>
                            <el-descriptions-item label="标题">
                                {{ props.data.title }}
                            </el-descriptions-item>
                            <el-descriptions-item label="链接">
                                <el-link :href="props.data.url" target="_blank" type="primary">
                                    {{ props.data.url }}
                                </el-link>
                            </el-descriptions-item>
                            <el-descriptions-item label="摘要">
                                {{ props.data.snippet }}
                            </el-descriptions-item>
                            <el-descriptions-item label="日期">
                                {{ props.data.date }}
                            </el-descriptions-item>
                            <el-descriptions-item label="位置">
                                {{ props.data.position }}
                            </el-descriptions-item>
                            <el-descriptions-item label="大小">
                                {{ formatFileSize(props.data.size) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="状态">
                                <el-tag :type="getStatusType()" size="small">
                                    {{ getStatusText() }}
                                </el-tag>
                            </el-descriptions-item>
                        </el-descriptions>
                    </div>

                    <!-- 内容部分 - 根据状态显示 -->
                    <div v-if="props.data.status === 'SUCCESS' || props.data.status === 'IGNORE'"
                        class="content-section">
                        <div class="section-header">
                            <h4>内容</h4>
                            <CopyButton :content="String(props.data.content || '')" />
                        </div>
                        <div class="extracted-content">
                            <MarkdownViewer :key="`content-${Date.now()}`" :value="String(props.data.content || '')" />
                        </div>
                    </div>

                    <!-- 错误部分 - FAIL状态显示 -->
                    <div v-else-if="props.data.status === 'FAIL'" class="error-section">
                        <div class="section-header">
                            <h4>错误信息</h4>
                            <CopyButton :content="String(props.data.content || '')" />
                        </div>
                        <div class="error-message">
                            <MarkdownViewer :key="`error-${Date.now()}`" :value="String(props.data.content || '')" />
                        </div>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import MarkdownViewer from '@/components/mdViewer/index.vue'
import CopyButton from '../copyButton.vue'
import type { AgentRuntime } from '../../agentRuntimeSupport';
import { formatFileSize } from '@/utils/tools'
interface Props {
    data: any
    agentRuntime: AgentRuntime
}

const props = defineProps<Props>()

// 获取状态类型
const getStatusType = (): string => {
    switch (props.data.status) {
        case 'SUCCESS':
            return 'success'
        case 'FAIL':
            return 'danger'
        case 'IGNORE':
            return 'info'
        case 'NOT_START':
        default:
            return 'warning'
    }
}

// 获取状态文本
const getStatusText = (): string => {
    switch (props.data.status) {
        case 'SUCCESS':
            return '成功'
        case 'FAIL':
            return '失败'
        case 'IGNORE':
            return '忽略'
        case 'NOT_START':
        default:
            return '未开始'
    }
}


</script>

<style scoped>
.url-display {
    height: 100%;
}

.display-card {
    height: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.title {
    font-weight: bold;
    font-size: 16px;
}

.content-container {

    overflow-y: auto;
    padding: 0;
}

.url-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.url-info h4,
.content-section h4,
.error-section h4 {
    margin: 0 0 10px 0;
    color: #303133;
    font-size: 14px;
    font-weight: 600;
}

.section-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    gap: 8px;
}

.section-header h4 {
    margin: 0;
}

.content-section,
.error-section {
    margin-top: 15px;
}

.extracted-content,
.error-message {
    margin-top: 10px;
}

/* 确保descriptions的label部分有固定宽度 */
:deep(.el-descriptions__label) {
    width: 96px !important;
    min-width: 96px !important;
    max-width: 96px !important;
    flex-shrink: 0 !important;
}

/* 确保descriptions的content部分能够正确换行 */
:deep(.el-descriptions__content) {
    word-break: break-all;
    word-wrap: break-word;
    overflow-wrap: break-word;
}
</style>
