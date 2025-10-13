<template>
    <div class="llm-display">
        <div class="content-container">
            <!-- 使用Tab方式显示请求消息和反馈消息 -->
            <el-tabs v-model="activeTab" class="llm-tabs">
                <!-- 反馈消息Tab -->
                <el-tab-pane name="feedback">
                    <template #label>
                        <div class="tab-label">
                            <span>反馈消息</span>
                            <CopyButton :content="fullFeedbackContent" />
                        </div>
                    </template>
                    <div class="tab-content">
                        <!-- 反馈消息内容 -->
                        <div v-if="llmData.responseContent" class="feedback-content">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item name="content">
                                    <template #title>
                                        <div class="collapse-title">
                                            <span>消息内容</span>
                                            <CopyButton :content="llmData.responseContent" />
                                        </div>
                                    </template>
                                    <MarkdownViewer :key="`feedback-content-${Date.now()}`"
                                        :value="llmData.responseContent" />
                                </el-collapse-item>
                            </el-collapse>
                        </div>

                        <!-- 反馈消息推理内容 -->
                        <div v-if="llmData.responseReasoning" class="feedback-reasoning">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item name="reasoning">
                                    <template #title>
                                        <div class="collapse-title">
                                            <span>推理内容</span>
                                            <CopyButton :content="llmData.responseReasoning" />
                                        </div>
                                    </template>
                                    <MarkdownViewer :key="`feedback-reasoning-${Date.now()}`"
                                        :value="llmData.responseReasoning" />
                                </el-collapse-item>
                            </el-collapse>
                        </div>

                        <!-- 反馈消息工具调用 -->
                        <div v-if="llmData.toolCalls && llmData.toolCalls.length > 0" class="feedback-tools">
                            <el-collapse v-model="feedbackCollapse">
                                <el-collapse-item name="tools">
                                    <template #title>
                                        <div class="collapse-title">
                                            <span>工具调用</span>
                                        </div>
                                    </template>
                                    <div class="tools-call">
                                        <el-card v-for="(toolCall, index) in llmData.toolCalls" :key="index"
                                            class="tool-call-item" shadow="hover">
                                            <div class="tool-call-header">
                                                <span class="tool-name">{{ toolCall.function?.name || '未知工具' }}</span>
                                                <div class="header-actions">
                                                    <el-tag size="small" type="success">调用</el-tag>
                                                    <CopyButton :content="formatToolCallContent(toolCall)" />
                                                </div>
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
                <el-tab-pane name="request">
                    <template #label>
                        <div class="tab-label">
                            <span>请求信息</span>
                            <CopyButton :content="fullRequestContent" />
                        </div>
                    </template>
                    <div class="tab-content">
                        <!-- 请求消息 -->
                        <div v-if="requestMessages && requestMessages.length > 0" class="request-messages">
                            <el-collapse v-model="requestCollapse">
                                <el-collapse-item name="messages">
                                    <template #title>
                                        <div class="collapse-title">
                                            <span>请求消息</span>
                                        </div>
                                    </template>
                                    <div class="messages-list">
                                        <div v-for="(message, index) in requestMessages" :key="index"
                                            class="message-item">
                                            <el-card shadow="hover" class="message-card">
                                                <div class="message-header">
                                                    <span class="message-role">{{ getRoleDisplayName(message.role)
                                                    }}</span>
                                                    <div class="header-actions">
                                                        <el-tag size="small" type="primary">{{ index + 1 }}</el-tag>
                                                        <CopyButton :content="formatMessageContent(message)" />
                                                    </div>
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
                                                                <div class="header-actions">
                                                                    <el-tag size="small" type="warning">工具</el-tag>
                                                                    <CopyButton
                                                                        :content="formatToolCallContent(toolCall)" />
                                                                </div>
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
                                                <div class="tool-name-group">
                                                    <span class="tool-name">{{ tool.function?.name || '未知工具' }}</span>
                                                    <CopyButton :content="formatToolDefinition(tool)" />
                                                </div>
                                                <el-tag size="small" type="info">function</el-tag>
                                            </div>
                                            <p class="tool-description">{{ tool.function?.description || '暂无描述' }}</p>
                                            <div v-if="tool.function?.parameters" class="tool-params">
                                                <h6>参数:</h6>
                                                <el-descriptions :column="1" size="small">
                                                    <el-descriptions-item
                                                        v-for="(param, key) in tool.function.parameters.properties"
                                                        :key="key" :label="String(key)">

                                                        {{ param.type }} - {{ param.description }}
                                                        <el-tag
                                                            v-if="isRequiredParam(String(key), tool.function.parameters.required)"
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
import CopyButton from '../copyButton.vue'
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

// 获取完整的反馈消息内容
const fullFeedbackContent = computed(() => {
    const parts: string[] = []

    if (llmData.value.responseContent) {
        parts.push('## 消息内容\n\n```markdown\n' + llmData.value.responseContent + '\n```')
    }

    if (llmData.value.responseReasoning) {
        parts.push('## 推理内容\n\n```markdown\n' + llmData.value.responseReasoning + '\n```')
    }

    if (llmData.value.toolCalls && llmData.value.toolCalls.length > 0) {
        const toolCallsStr = llmData.value.toolCalls.map((toolCall: any, index: number) => {
            return `### 工具调用 ${index + 1}\n\n**工具名称:** ${toolCall.function?.name || '未知工具'}\n\n**参数:**\n\`\`\`json\n${formatArguments(toolCall.function?.arguments)}\n\`\`\``
        }).join('\n\n')
        parts.push('## 工具调用\n\n' + toolCallsStr)
    }

    return parts.join('\n\n')
})

// 获取完整的请求消息内容
const fullRequestContent = computed(() => {
    return llmData.value.requestData || ''
})

// 格式化工具调用内容
const formatToolCallContent = (toolCall: any): string => {
    return `**工具名称:** ${toolCall.function?.name || '未知工具'}\n\n**参数:**\n\`\`\`json\n${formatArguments(toolCall.function?.arguments)}\n\`\`\``
}

// 格式化消息内容
const formatMessageContent = (message: any): string => {
    const parts: string[] = []

    parts.push(`**角色:** ${getRoleDisplayName(message.role)}`)

    if (message.content) {
        parts.push(`\n**内容:**\n\n${message.content}`)
    }

    if (message.reasoning_content) {
        parts.push(`\n**推理内容:**\n\n${message.reasoning_content}`)
    }

    if (message.tool_calls && message.tool_calls.length > 0) {
        const toolCallsStr = message.tool_calls.map((toolCall: any, index: number) => {
            return `**工具 ${index + 1}:** ${toolCall.function?.name}\n\`\`\`json\n${formatArguments(toolCall.function?.arguments)}\n\`\`\``
        }).join('\n\n')
        parts.push(`\n**工具调用:**\n\n${toolCallsStr}`)
    }

    return parts.join('\n')
}

// 格式化工具定义为JSON
const formatToolDefinition = (tool: any): string => {
    try {
        return JSON.stringify(tool, null, 2)
    } catch {
        return JSON.stringify(tool)
    }
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

.header-actions {
    display: flex;
    align-items: center;
    gap: 8px;
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

.tool-name-group {
    display: flex;
    align-items: center;
    gap: 8px;
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

/* Tab标签样式 */
.tab-label {
    display: flex;
    align-items: center;
    gap: 8px;
}

/* 折叠面板标题样式 */
.collapse-title {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
}
</style>
