import http from '@/utils/request'
import { ElMessage } from 'element-plus'

/**
 * 获取文件二进制内容（私有方法）
 * @param sessionId 会话ID
 * @param fileName 文件名
 * @returns Promise<ArrayBuffer> 文件二进制内容
 */
const getFileContentBinary = async (sessionId: string, fileName: string): Promise<ArrayBuffer> => {
    try {
        const response = await http.get('/chat/fileDownload', {
            params: {
                sessionId,
                fileName
            },
            responseType: 'arraybuffer' // 设置响应类型为二进制
        })

        return response.data
    } catch (error) {
        ElMessage.error('获取文件内容失败')
        throw error
    }
}

/**
 * 获取文件内容（文本）
 * @param sessionId 会话ID
 * @param fileName 文件名
 * @returns Promise<string> 文件内容
 */
export const getFileContent = async (sessionId: string, fileName: string): Promise<string> => {
    try {
        const arrayBuffer = await getFileContentBinary(sessionId, fileName)

        // 将 ArrayBuffer 转换为文本，默认使用 UTF-8 编码
        const decoder = new TextDecoder('utf-8')
        return decoder.decode(arrayBuffer)
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
        // 获取文件二进制内容
        const arrayBuffer = await getFileContentBinary(sessionId, fileName)

        // 创建blob并下载
        const blob = new Blob([arrayBuffer])
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
        ElMessage.error('下载文件失败')
        throw error
    }
}
