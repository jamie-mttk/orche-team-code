<template>
    <div class="llm-display">
        <div class="content-container">
            <!-- 使用Tab方式显示请求消息和反馈消息 -->
            <el-tabs v-model="activeTab" class="llm-tabs">
                <!-- 反馈消息Tab -->
                <el-tab-pane label="反馈消息" name="feedback">
                    <div class="tab-content">
                        <!-- 反馈消息内容 -->
                        <div v-if="llmData.responseContent" class="feedback-content">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item title="消息内容" name="content">
                                    <MarkdownViewer :key="`feedback-content-${Date.now()}`"
                                        :value="llmData.responseContent" />
                                </el-collapse-item>
                            </el-collapse>
                        </div>

                        <!-- 反馈消息推理内容 -->
                        <div v-if="llmData.responseReasoning" class="feedback-reasoning">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item title="推理内容" name="reasoning">
                                    <MarkdownViewer :key="`feedback-reasoning-${Date.now()}`"
                                        :value="llmData.responseReasoning" />
                                </el-collapse-item>
                            </el-collapse>
                        </div>

                        <!-- 反馈消息工具调用 -->
                        <div v-if="llmData.toolCalls && llmData.toolCalls.length > 0" class="feedback-tools">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item title="工具调用" name="tools">
                                    <div class="tools-call">
                                        <el-card v-for="(toolCall, index) in llmData.toolCalls" :key="index"
                                            class="tool-call-item" shadow="hover">
                                            <div class="tool-call-header">
                                                <span class="tool-name">{{ toolCall.function?.name || '未知工具' }}</span>
                                                <el-tag size="small" type="success">调用</el-tag>
                                            </div>
                                            <div class="tool-call-args" v-if="toolCall.function?.arguments">
                                                <strong>参数:</strong>
                                                <MarkdownViewer :key="`tool-${index}-${Date.now()}`"
                                                    :value="formatArguments(toolCall.function.arguments)" />
                                            </div>
                                        </el-card>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                        </div>
                    </div>
                </el-tab-pane>

                <!-- 请求信息Tab -->
                <el-tab-pane label="请求信息" name="request">
                    <div class="tab-content">
                        <!-- 请求消息 -->
                        <div v-if="requestMessages && requestMessages.length > 0" class="request-messages">
                            <el-collapse v-model="requestCollapse">
                                <el-collapse-item title="请求消息" name="messages">
                                    <div class="messages-list">
                                        <div v-for="(message, index) in requestMessages" :key="index"
                                            class="message-item">
                                            <el-card shadow="hover" class="message-card">
                                                <div class="message-header">
                                                    <span class="message-role">{{ getRoleDisplayName(message.role)
                                                    }}</span>
                                                    <el-tag size="small" type="primary">{{ index + 1 }}</el-tag>
                                                </div>

                                                <!-- 消息内容 -->
                                                <div class="message-content" v-if="message.content">
                                                    <MarkdownViewer :key="`message-content-${index}-${Date.now()}`"
                                                        :value="message.content" />
                                                </div>

                                                <!-- 推理内容 -->
                                                <div class="message-reasoning" v-if="message.reasoning_content">
                                                    <h6>推理内容:</h6>
                                                    <MarkdownViewer :key="`message-reasoning-${index}-${Date.now()}`"
                                                        :value="message.reasoning_content" />
                                                </div>

                                                <!-- 工具调用 -->
                                                <div class="message-tools"
                                                    v-if="message.tool_calls && message.tool_calls.length > 0">
                                                    <h6>工具调用:</h6>
                                                    <div class="tool-calls-list">
                                                        <div v-for="(toolCall, toolIndex) in message.tool_calls"
                                                            :key="toolIndex" class="tool-call-detail">
                                                            <div class="tool-call-info">
                                                                <span class="tool-name">{{ toolCall.function?.name
                                                                }}</span>
                                                                <el-tag size="small" type="warning">工具</el-tag>
                                                            </div>
                                                            <div class="tool-args" v-if="toolCall.function?.arguments">
                                                                <strong>参数:</strong>
                                                                <MarkdownViewer
                                                                    :key="`request-tool-${index}-${toolIndex}-${Date.now()}`"
                                                                    :value="formatArguments(toolCall.function.arguments)" />
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </el-card>
                                        </div>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                        </div>

                        <!-- 可用工具 -->
                        <div v-if="requestTools && requestTools.length > 0" class="request-tools">
                            <el-collapse v-model="requestCollapse">
                                <el-collapse-item title="可用工具" name="tools">
                                    <div class="tools-list">
                                        <el-card v-for="(tool, index) in requestTools" :key="index" class="tool-item"
                                            shadow="hover">
                                            <div class="tool-header">
                                                <span class="tool-name">{{ tool.function?.name || '未知工具' }}</span>
                                                <el-tag size="small" type="info">function</el-tag>
                                            </div>
                                            <p class="tool-description">{{ tool.function?.description || '暂无描述' }}</p>
                                            <div v-if="tool.function?.parameters" class="tool-params">
                                                <h6>参数:</h6>
                                                <el-descriptions :column="1" size="small">
                                                    <el-descriptions-item
                                                        v-for="(param, key) in tool.function.parameters.properties"
                                                        :key="key" :label="key">

                                                        {{ param.type }} - {{ param.description }}
                                                        <el-tag
                                                            v-if="isRequiredParam(key, tool.function.parameters.required)"
                                                            size="small" type="danger" class="required-tag">必需</el-tag>
                                                    </el-descriptions-item>
                                                </el-descriptions>
                                            </div>
                                        </el-card>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import MarkdownViewer from '@/components/mdViewer/index.vue'
import type { AgentRuntime } from '../../agentRuntimeSupport'

interface Props {
    data: any
    agentRuntime: AgentRuntime
}

const props = defineProps<Props>()

// 当前激活的Tab
const activeTab = ref('feedback')

// 折叠面板激活状态
const feedbackCollapse = ref(['content', 'reasoning', 'tools'])
const requestCollapse = ref(['messages', 'tools'])

// LLM数据
const llmData = computed(() => {
    return {
        requestData: props.data?.requestData ?? '',
        responseContent: props.data?.responseContent ?? '',
        responseReasoning: props.data?.responseReasoning ?? '',
        toolCalls: props.data?.toolCalls ?? []
    }
})

// 获取角色显示名称
const getRoleDisplayName = (role: string): string => {
    switch (role.toLowerCase()) {
        case 'user':
            return '用户'
        case 'system':
            return '系统'
        case 'assistant':
            return '助手'
        default:
            return role
    }
}

// 解析请求消息
const requestMessages = computed(() => {
    if (!llmData.value.requestData) return []

    try {
        const request = JSON.parse(llmData.value.requestData)
        if (request.messages && Array.isArray(request.messages)) {
            // 从后向前显示消息
            return [...request.messages].reverse()
        }
        return []
    } catch (error) {
        ElMessage.error('解析请求数据失败')
        return []
    }
})

// 解析请求中的工具
const requestTools = computed(() => {
    if (!llmData.value.requestData) return []

    try {
        const request = JSON.parse(llmData.value.requestData)
        if (request.tools && Array.isArray(request.tools)) {
            return request.tools
        }
        return []
    } catch (error) {
        ElMessage.error('解析请求工具失败')
        return []
    }
})



// 格式化参数
const formatArguments = (args: string | undefined): string => {
    if (!args) return '无参数'
    try {
        return JSON.stringify(JSON.parse(args), null, 2)
    } catch {
        return args
    }
}

// 判断参数是否为必需参数
const isRequiredParam = (paramKey: string, requiredParams: string[] | undefined): boolean => {
    return requiredParams ? requiredParams.includes(paramKey) : false
}
</script>

<style scoped>
.llm-display {
    height: 100%;
}

.content-container {

    overflow-y: auto;
    padding: 20px;
}

.llm-tabs {
    height: 100%;
}

.tab-content {
    padding: 20px 0;
}

.feedback-content,
.feedback-reasoning,
.feedback-tools,
.request-messages,
.request-tools {
    margin-bottom: 20px;
}

.messages-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.message-item {
    margin-bottom: 10px;
}

.message-card {
    border: 1px solid #e4e7ed;
}

.message-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.message-role {
    font-weight: 600;
    color: #303133;
    text-transform: capitalize;
}

.message-content,
.message-reasoning,
.message-tools {
    margin-top: 15px;
}

.tool-calls-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.tool-call-detail {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 10px;
    background-color: #fafafa;
}

.tool-call-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.tool-name {
    font-weight: 600;
    color: #303133;
}

.tool-args {
    margin-top: 8px;
}

.tool-args strong {
    color: #303133;
    font-size: 13px;
}

.tools-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.tool-item {
    border: 1px solid #e4e7ed;
}

.tool-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.tool-description {
    margin: 8px 0;
    color: #606266;
    font-size: 13px;
    line-height: 1.4;
}

.tool-params {
    margin-top: 10px;
}

.tools-call {
    margin-top: 15px;
}

.tool-call-item {
    margin-bottom: 10px;
    border: 1px solid #e4e7ed;
}

.tool-call-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.tool-call-args {
    margin-top: 8px;
}

.tool-call-args strong {
    color: #303133;
    font-size: 13px;
}

/* Tab样式优化 */
:deep(.el-tabs__header) {
    margin-bottom: 0;
}

:deep(.el-tabs__content) {
    height: calc(100% - 40px);
    overflow-y: auto;
}

/* 折叠面板样式优化 */
:deep(.el-collapse-item__header) {
    font-weight: 600;
    color: #303133;
}

:deep(.el-collapse-item__content) {
    padding: 15px 0;
}

.required-tag {
    margin-left: 8px;
}
</style>
