// @ts-ignore
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { ElMessage } from 'element-plus'

const SSE_HEADERS = {
    'Content-Type': 'application/json;charset=utf-8',
    'Accept': 'text/event-stream',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive'
}

interface SSEConfig {
    body: any
    handleMessage: (data: any) => void
    handleError: (error: Error) => void
    handleClose: () => void
    handleOpen?: (response: Response) => void
    signal?: AbortSignal
}

/**
 * 创建服务器发送事件（SSE）连接
 * @param url SSE连接URL
 * @param config SSE配置
 */
export const createSSEConnection = (url: string, config: SSEConfig): void => {
    const {
        body,
        handleMessage,
        handleError,
        handleClose,
        handleOpen,
        signal
    } = config

    fetchEventSource(url, {
        method: 'POST',
        headers: SSE_HEADERS,
        body: JSON.stringify(body),
        signal,
        openWhenHidden: true,

        // 连接成功回调
        onopen: async (response: Response) => {
            if (handleOpen) {
                handleOpen(response)
            } else {
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
            }
        },

        // 消息处理回调
        onmessage: (event: any) => {
            try {
                // 检查是否为结束标志
                if (event.data === '[DONE]') {
                    handleClose()
                    return
                }

                // 解析JSON消息
                const parsedData = JSON.parse(event.data)
                handleMessage(parsedData)
            } catch (error) {
                console.error('Error parsing SSE message:', error, event)
                handleError(new Error('Failed to parse SSE message'))
            }
        },

        // 连接关闭回调
        onclose: () => {
            ElMessage.success('对话完成')
            handleClose()
        },

        // 错误处理回调
        onerror: (error: Error) => {

            // 根据错误类型显示不同的提示
            if (error.name === 'AbortError') {
                ElMessage.info('连接已断开')
            } else if (error.name === 'NetworkError') {
                ElMessage.error('网络连接错误')
            } else if (error.name === 'TypeError') {
                ElMessage.error('连接类型错误')
            } else {
                ElMessage.error('流式传输失败: ' + (error?.message || '未知错误'))
            }
            //
            handleError(error)
            throw error // 重新抛出错误以触发重试机制
        }
    })
}

export default createSSEConnection