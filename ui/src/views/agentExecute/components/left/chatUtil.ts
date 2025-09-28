import { ElMessage } from 'element-plus'
import request from '@/utils/request'

/**
 * 停止任务执行
 * @param sessionId 会话ID
 * @returns Promise<boolean> 是否成功发送停止请求
 */
export const stopTaskExecution = async (sessionId: string): Promise<boolean> => {
    if (!sessionId) {
        ElMessage.error('会话ID不存在,无法停止执行')
        return false
    }

    try {
        const response = await request.post(`/chat/cancel/${sessionId}`)
        const result = response.data.result

        switch (result) {
            case 'NOT_FOUND':
                ElMessage.error('未找到对应的会话记录')
                return false
            case 'INVALID':
                ElMessage.error('当前任务状态不是运行中，无法停止')
                return false
            case 'REQUESTED':
                ElMessage.success('已发送停止请求')
                return true
            case 'DUPLICATED':
                ElMessage.warning('重复的停止请求')
                return false
            default:
                ElMessage.error('未知的返回结果:' + result)
                return false
        }
    } catch (error) {
        console.error('停止执行失败:', error)
        ElMessage.error('停止执行失败: ' + (error as Error).message)
        return false
    }
}
