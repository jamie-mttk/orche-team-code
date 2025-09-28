import http from '@/utils/request'
import { ElMessage } from 'element-plus'

/**
 * 获取文件内容
 * @param sessionId 会话ID
 * @param fileName 文件名
 * @returns Promise<string> 文件内容
 */
export const getFileContent = async (sessionId: string, fileName: string): Promise<string> => {
    try {
        const response = await http.get('/chat/fileDownload', {
            params: {
                sessionId,
                fileName
            },
            responseType: 'text' // 设置响应类型为文本
        })

        return response.data
    } catch (error) {

        ElMessage.error('获取文件内容失败')
        throw error
    }
}

/**
 * 下载文件
 * @param sessionId 会话ID
 * @param fileName 文件名
 * @returns Promise<void>
 */
export const downloadFile = async (sessionId: string, fileName: string): Promise<void> => {
    try {
        // 先获取文件内容
        const fileContent = await getFileContent(sessionId, fileName)

        // 创建blob并下载
        const blob = new Blob([fileContent])
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)

        ElMessage.success('文件下载成功')
    } catch (error) {
        // 错误已在getFileContent中处理
        ElMessage.error('下载文件失败')
        throw error
    }
}
