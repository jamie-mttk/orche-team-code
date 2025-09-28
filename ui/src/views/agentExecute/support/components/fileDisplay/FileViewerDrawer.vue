<template>
    <el-drawer v-model="visible" :title="fileName" direction="rtl" :size="'70%'" :before-close="handleClose">
        <div class="file-viewer-content">


            <!-- 文件内容 -->
            <div class="file-content">
                <!-- 空内容提示 -->
                <div v-if="!fileContent || fileContent.trim() === ''" class="no-content">
                    <el-alert title="文件内容为空" description="当前文件没有内容可以显示，或者文件内容为空。" type="warning" :closable="false"
                        show-icon />
                    <div class="debug-info">
                        <p><strong>调试信息:</strong></p>
                        <p>文件名: {{ { fileName } }}</p>
                        <p>文件类型: {{ getFileExtension() }}</p>
                        <p>内容长度: {{ fileContent ? fileContent.length : 0 }}</p>
                        <p>内容预览: {{ fileContent ? fileContent.substring(0, 100) + '...' : '无内容' }}</p>
                    </div>
                </div>

                <!-- HTML文件 -->
                <div v-else-if="isHtmlFile" class="html-content" :style="{ height: contentHeight + 'px' }">
                    <iframe :src="htmlContentUrl" width="100%" height="100%" frameborder="0"
                        class="html-iframe"></iframe>
                </div>

                <!-- Markdown文件 -->
                <div v-else-if="isMarkdownFile" class="markdown-content" :style="{ height: contentHeight + 'px' }">
                    <div class="markdown-renderer" v-html="renderedMarkdown"></div>
                </div>

                <!-- 其他文件类型 -->
                <div v-else class="monaco-content">
                    <MonacoEditor :value="fileContent" :language="getLanguage()" :readonly="true"
                        :height="contentHeight" :options="monacoOptions" />
                </div>
            </div>

            <!-- 操作按钮 -->
            <div class="file-actions">
                <el-button type="primary" @click="handleDownload">
                    <Icon name="mdiDownload" />
                    下载文件
                </el-button>
                <el-button type="success" @click="handlePrint">
                    <Icon name="mdiPrinter" />
                    打印文件
                </el-button>
                <el-button @click="handleClose">
                    关闭
                </el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import MonacoEditor from 'monaco-editor-vue3'
import { marked } from 'marked'
import { downloadFile } from './fileUtils.ts'

interface Props {
    modelValue: boolean
    fileName: string
    fileContent: string
    sessionId: string
    fileDescription?: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
    'update:modelValue': [value: boolean]
}>()

const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
})

// 文件类型判断
const isHtmlFile = computed(() => {
    const ext = getFileExtension().toLowerCase()
    return ext === 'html' || ext === 'htm' || ext === 'ppt'
})

const isMarkdownFile = computed(() => {
    const ext = getFileExtension().toLowerCase()
    return ext === 'md'
})

// 获取文件扩展名
const getFileExtension = (): string => {
    return props.fileName.split('.').pop() || ''
}

// 获取Monaco Editor语言
const getLanguage = (): string => {
    const ext = getFileExtension().toLowerCase()
    switch (ext) {
        case 'json':
            return 'json'
        case 'js':
        case 'ts':
            return 'javascript'
        case 'html':
        case 'htm':
            return 'html'
        case 'css':
            return 'css'
        case 'py':
            return 'python'
        case 'java':
            return 'java'
        case 'cpp':
        case 'c':
            return 'cpp'
        default:
            return 'text'
    }
}

// HTML内容URL（用于iframe显示）
const htmlContentUrl = computed(() => {
    if (isHtmlFile.value) {
        const blob = new Blob([props.fileContent], { type: 'text/html' })
        return URL.createObjectURL(blob)
    }
    return ''
})

// 渲染的Markdown内容
const renderedMarkdown = computed(() => {
    if (isMarkdownFile.value) {
        try {
            return marked(props.fileContent)
        } catch (error) {
            ElMessage.error('Markdown渲染失败')
            return props.fileContent
        }
    }
    return ''
})

// 内容区域高度计算
const contentHeight = computed(() => {
    // 计算可用高度：drawer高度 - 文件信息高度 - 操作按钮高度 - 内边距
    // 假设drawer高度为70%，文件信息约80px，操作按钮约60px，内边距40px
    const drawerHeight = window.innerHeight
    const availableHeight = drawerHeight - 80 - 60 - 40
    return Math.max(availableHeight, 400) // 最小高度400px
})

// Monaco Editor选项
const monacoOptions = {
    readOnly: true,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    theme: 'vs-dark',
    automaticLayout: true,
    scrollbar: {
        vertical: 'visible',
        horizontal: 'visible'
    }
}

// 下载文件
const handleDownload = async () => {
    try {
        await downloadFile(props.sessionId, props.fileName)
    } catch (error) {
        // 错误已在downloadFile中处理
        ElMessage.error('下载文件失败')
    }
}

// 打印文件
const handlePrint = () => {
    const printWindow = window.open('', '_blank')
    if (!printWindow) {
        ElMessage.error('无法打开打印窗口，请检查浏览器弹窗设置')
        return
    }

    let printContent = ''
    let title = props.fileName

    if (isHtmlFile.value) {
        // HTML文件：打印渲染后的HTML页面
        printContent = `
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>${title}</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        margin: 20px; 
                        line-height: 1.6;
                    }
                    /* 继承原始HTML的样式 */
                    * { box-sizing: border-box; }
                    h1, h2, h3, h4, h5, h6 { 
                        margin-top: 20px; 
                        margin-bottom: 15px; 
                        font-weight: 600; 
                    }
                    p { margin-bottom: 15px; }
                    img { max-width: 100%; height: auto; }
                    table { border-collapse: collapse; width: 100%; margin: 15px 0; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #f5f5f5; }
                    code { background: #f5f5f5; padding: 2px 4px; border-radius: 3px; }
                    pre { background: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; }
                    blockquote { border-left: 4px solid #ddd; padding-left: 15px; margin: 15px 0; }
                    ul, ol { padding-left: 20px; }
                    li { margin-bottom: 5px; }
                </style>
            </head>
            <body>
                ${props.fileContent}
            </body>
            </html>
        `
    } else if (isMarkdownFile.value) {
        // Markdown文件：打印渲染后的格式化内容
        try {
            const renderedContent = marked(props.fileContent)
            printContent = `
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>${title}</title>
                    <style>
                        body { 
                            font-family: Arial, sans-serif; 
                            margin: 20px; 
                            line-height: 1.6;
                            color: #333;
                        }
                        h1, h2, h3, h4, h5, h6 { 
                            margin-top: 24px; 
                            margin-bottom: 16px; 
                            font-weight: 600; 
                            line-height: 1.25;
                        }
                        h1 { 
                            font-size: 2em; 
                            border-bottom: 1px solid #eaecef; 
                            padding-bottom: 0.3em; 
                        }
                        h2 { 
                            font-size: 1.5em; 
                            border-bottom: 1px solid #eaecef; 
                            padding-bottom: 0.3em; 
                        }
                        h3 { font-size: 1.25em; }
                        h4 { font-size: 1em; }
                        h5 { font-size: 0.875em; }
                        h6 { font-size: 0.85em; color: #606266; }
                        p { margin-bottom: 16px; }
                        ul, ol { margin-bottom: 16px; padding-left: 24px; }
                        li { margin-bottom: 8px; }
                        blockquote { 
                            margin: 16px 0; 
                            padding: 0 16px; 
                            color: #606266; 
                            border-left: 4px solid #e4e7ed; 
                            background-color: #f8f9fa; 
                        }
                        code { 
                            padding: 0.2em 0.4em; 
                            margin: 0; 
                            
                            background-color: rgba(27, 31, 35, 0.05); 
                            border-radius: 3px; 
                            color: #e83e8c; 
                            font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
                        }
                        pre { 
                            padding: 16px; 
                            overflow: auto; 
                
                            line-height: 1.45; 
                            background-color: #f6f8fa; 
                            border-radius: 3px; 
                            border: 1px solid #e1e4e8; 
                        }
                        pre code { 
                            padding: 0; 
                            background-color: transparent; 
                            color: #24292e; 
                        }
                        table { 
                            border-collapse: collapse; 
                            width: 100%; 
                            margin: 16px 0; 
                        }
                        th, td { 
                            padding: 8px 12px; 
                            border: 1px solid #e1e4e8; 
                            text-align: left; 
                        }
                        th { 
                            background-color: #f6f8fa; 
                            font-weight: 600; 
                            color: #24292e; 
                        }
                        tr:nth-child(even) { background-color: #f6f8fa; }
                        hr { 
                            height: 1px; 
                            background-color: #e1e4e8; 
                            border: none; 
                            margin: 24px 0; 
                        }
                        a { color: #0366d6; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                        img { max-width: 100%; height: auto; border-radius: 4px; }
                        strong { font-weight: 600; color: #24292e; }
                        em { font-style: italic; color: #24292e; }
                        del { text-decoration: line-through; color: #6a737d; }
                    </style>
                </head>
                <body>
                    ${renderedContent}
                </body>
                </html>
            `
        } catch (error) {
            ElMessage.error('Markdown渲染失败，无法打印')
            return
        }
    } else {
        // 其他文件类型：打印代码内容
        printContent = `
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>${title}</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    pre { background: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; }
                    code { font-family: 'Courier New', monospace; white-space: pre-wrap; }
                    .file-header { margin-bottom: 20px; }
                    .file-header h1 { color: #333; margin-bottom: 10px; }
                    .file-header .file-info { color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="file-header">
                    <h1>${title}</h1>
                    <div class="file-info">
                        文件类型: ${getFileExtension()}<br>
                        语言: ${getLanguage()}<br>
                        内容长度: ${props.fileContent.length} 字符
                    </div>
                </div>
                <hr>
                <pre><code class="language-${getLanguage()}">${escapeHtml(props.fileContent)}</code></pre>
            </body>
            </html>
        `
    }

    // 写入内容并打印
    printWindow.document.write(printContent)
    printWindow.document.close()

    // 等待内容加载完成后打印
    printWindow.onload = () => {
        printWindow.print()
        printWindow.close()
    }
}

// HTML转义函数
const escapeHtml = (text: string): string => {
    const div = document.createElement('div')
    div.textContent = text
    return div.innerHTML
}

// 关闭drawer
const handleClose = () => {
    visible.value = false
}

// 清理HTML blob URL
watch(() => visible.value, (newValue) => {
    if (!newValue && htmlContentUrl.value) {
        URL.revokeObjectURL(htmlContentUrl.value)
    }
})
</script>

<style scoped>
.file-viewer-content {
    height: 100%;
    display: flex;
    flex-direction: column;

}

.file-info {
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 6px;
}

.file-name {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    line-height: 1.4;
    word-break: break-all;
}

.file-description {
    font-size: 14px;
    color: #606266;
    line-height: 1.5;
    word-break: break-all;
}

.file-content {
    flex: 1;
    overflow: hidden;
    margin-bottom: 20px;
}

.no-content {
    padding: 20px;
    text-align: center;
}

.debug-info {
    margin-top: 20px;
    padding: 15px;
    background-color: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 4px;
    text-align: left;
}

.debug-info p {
    margin: 8px 0;
    font-family: monospace;
    font-size: 14px;
}

.html-content {
    overflow: hidden;
}

.html-iframe {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    background: white;
}

.markdown-content {
    overflow-y: auto;
    padding: 20px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    background: white;
}

.markdown-renderer {
    line-height: 1.6;
}

.markdown-renderer h1,
.markdown-renderer h2,
.markdown-renderer h3,
.markdown-renderer h4,
.markdown-renderer h5,
.markdown-renderer h6 {
    margin-top: 24px;
    margin-bottom: 16px;
    font-weight: 600;
    line-height: 1.25;
}

.markdown-renderer h1 {
    font-size: 2em;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

.markdown-renderer h2 {
    font-size: 1.5em;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

.markdown-renderer p {
    margin-bottom: 16px;
}

.markdown-renderer code {
    padding: 0.2em 0.4em;
    margin: 0;

    background-color: rgba(27, 31, 35, 0.05);
    border-radius: 3px;
}

.markdown-renderer pre {
    padding: 16px;
    overflow: auto;

    line-height: 1.45;
    background-color: #f6f8fa;
    border-radius: 3px;
}

.markdown-renderer pre code {
    padding: 0;
    background-color: transparent;
}

.monaco-content {
    height: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.file-actions {
    display: flex;
    justify-content: center;
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #e4e7ed;
}
</style>
