import { ElMessage } from 'element-plus'
import config from '@/config/env'
import { ExecutionStatus, type AgentRuntime, type ExecutionStatusType } from './agentRuntimeSupport'
import { createSSEConnection } from '@/utils/SSEUtil'

export const callApiInternal = async (agentRuntime: AgentRuntime, uri: string, body: any = {}) => {

    agentRuntime.status.value = ExecutionStatus.EXECUTING
    try {
        //
        body.agentId = agentRuntime.agentId.value
        body.sessionId = agentRuntime.sessionId.value

        // 构建SSE URL
        // 生产环境需要获取当前访问服务器的完整URL
        let baseUrl = config.apiBaseURL
        if (config.isProd && baseUrl === '/') {
            // 在生产环境中，使用当前访问服务器的完整URL
            baseUrl = `${window.location.protocol}//${window.location.host}`
        }
        const sseUrl = `${baseUrl}/chat/${uri}`
        // 使用SSE工具类创建连接
        createSSEConnection(sseUrl, {
            body: body,
            // 连接成功回调
            handleOpen: (response: Response) => {
                if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
                    ElMessage.success('已连接到服务器')
                    return // 一切正常
                } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
                    // 客户端错误，不应该重试
                    throw new Error(`HTTP error! status: ${response.status}`)
                } else {
                    // 其他错误，可以重试
                    throw new Error(`HTTP error! status: ${response.status}`)
                }
            },
            // 消息处理回调
            handleMessage: (message: any) => {
                agentRuntime.sendMessage(message)
            },
            // 连接关闭回调
            handleClose: () => {
                agentRuntime.status.value = ExecutionStatus.IDLE
            },
            // 错误处理回调
            handleError: (_error: Error) => {
                agentRuntime.status.value = ExecutionStatus.IDLE
            }
        })
    } catch (error: any) {
        ElMessage.error('发送消息失败: ' + (error as Error).message)
        // 发生错误时重置状态
        agentRuntime.status.value = 'idle' as ExecutionStatusType
        throw error
    }
}