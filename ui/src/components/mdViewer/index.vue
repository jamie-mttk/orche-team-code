<template>
    <div class="markdown-viewer">
        <div class="markdown-content" v-html="renderedMarkdown"></div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'

interface Props {
    value: string
}

const props = defineProps<Props>()

// 检测内容类型的函数
const detectContentType = (content: string): 'html' | 'markdown' | 'json' => {
    const trimmedContent = content.trim()

    // 检测是否以<!DOCTYPE html>开头
    if (trimmedContent.startsWith('<!DOCTYPE html>')) {
        return 'html'
    }

    // 如果内容以HTML标签开始和结束，或者包含完整的HTML结构
    if (trimmedContent.startsWith('<') && trimmedContent.endsWith('>')) {
        return 'html'
    }

    // 检测是否包含HTML标签
    const htmlTagRegex = /<[^>]*>/g
    const hasHtmlTags = htmlTagRegex.test(content)

    // 如果包含HTML标签且没有明显的markdown语法，则认为是HTML
    if (hasHtmlTags) {
        const markdownPatterns = /^[#*`\-+>\s]|\[.*\]\(.*\)/gm
        const hasMarkdownSyntax = markdownPatterns.test(content)

        if (!hasMarkdownSyntax) {
            return 'html'
        }
    }


    return 'markdown'
}

// 处理嵌套的markdown代码块
const processNestedMarkdown = (content: string): string => {
    // 匹配 ```markdown ... ``` 代码块
    const markdownCodeBlockRegex = /```markdown\s*\n([\s\S]*?)```/g

    return content.replace(markdownCodeBlockRegex, (match, innerContent) => {
        try {
            // 递归渲染嵌套的markdown内容
            const renderedInner = marked(innerContent.trim())
            // 用一个特殊的div包裹，添加样式区分
            return `<div class="nested-markdown">${renderedInner}</div>`
        } catch (error) {
            console.error('嵌套Markdown渲染失败:', error)
            return match // 如果渲染失败，保留原始内容
        }
    })
}

// 渲染的Markdown内容
const renderedMarkdown = computed(() => {
    if (!props.value) return ''

    const contentType = detectContentType(props.value)

    let value = undefined
    if (contentType === 'html') {
        // 如果是HTML内容，使用Markdown语法创建代码块
        value = '```html\n' + props.value + '\n```'
    } else {
        value = props.value
    }

    try {
        // 先处理嵌套的markdown代码块
        const processedValue = processNestedMarkdown(value)
        // 然后使用marked处理整体内容
        return marked(processedValue)
    } catch (error) {
        console.error('Markdown渲染失败:', error)
        return value
    }
})
</script>

<style scoped>
.markdown-viewer {
    width: 100%;
    height: 100%;
}

.markdown-content {
    line-height: 1.6;
    padding: 16px;
    background: white;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    overflow-y: auto;
    overflow-x: hidden;
    max-height: 100%;
    word-wrap: break-word;
    word-break: break-all;
    white-space: pre-wrap;
    overflow-wrap: break-word;
}

/* Markdown样式 */
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
    margin-top: 12px;
    margin-bottom: 8px;
    font-weight: 600;
    line-height: 1.25;
    color: #303133;
}

.markdown-content :deep(h1) {
    font-size: 2em;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

.markdown-content :deep(h2) {
    font-size: 1.5em;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

.markdown-content :deep(h3) {
    font-size: 1.25em;
}

.markdown-content :deep(h4) {
    font-size: 1em;
}

.markdown-content :deep(h5) {
    font-size: 0.875em;
}

.markdown-content :deep(h6) {
    font-size: 0.85em;
    color: #606266;
}

.markdown-content :deep(p) {
    margin-bottom: 8px;
    color: #303133;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
    margin-bottom: 8px;
    padding-left: 24px;
}

.markdown-content :deep(li) {
    margin-bottom: 4px;
    color: #303133;
}

.markdown-content :deep(blockquote) {
    margin: 16px 0;
    padding: 0 16px;
    color: #606266;
    border-left: 4px solid #e4e7ed;
    background-color: #f8f9fa;
}

.markdown-content :deep(code) {
    padding: 0.2em 0.4em;
    margin: 0;

    background-color: rgba(27, 31, 35, 0.05);
    border-radius: 3px;
    color: #e83e8c;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.markdown-content :deep(pre) {
    padding: 16px;
    overflow-x: hidden;
    overflow-y: hidden;

    line-height: 1.45;
    background-color: #f6f8fa;
    border-radius: 3px;
    border: 1px solid #e1e4e8;
    white-space: pre-wrap;
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
}

.markdown-content :deep(pre code) {
    padding: 0;
    background-color: transparent;
    color: #24292e;
}

.markdown-content :deep(table) {
    border-collapse: collapse;
    width: 100%;
    margin: 8px 0;
    table-layout: fixed;
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
    padding: 8px 12px;
    border: 1px solid #e1e4e8;
    text-align: left;
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
}

.markdown-content :deep(th) {
    background-color: #f6f8fa;
    font-weight: 600;
    color: #24292e;
}

.markdown-content :deep(tr:nth-child(even)) {
    background-color: #f6f8fa;
}

.markdown-content :deep(hr) {
    height: 1px;
    background-color: #e1e4e8;
    border: none;
    margin: 24px 0;
}

.markdown-content :deep(a) {
    color: #0366d6;
    text-decoration: none;
}

.markdown-content :deep(a:hover) {
    text-decoration: underline;
}

.markdown-content :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
}

.markdown-content :deep(strong) {
    font-weight: 600;
    color: #24292e;
}

.markdown-content :deep(em) {
    font-style: italic;
    color: #24292e;
}

.markdown-content :deep(del) {
    text-decoration: line-through;
    color: #6a737d;
}

/* 嵌套markdown样式 */
.markdown-content :deep(.nested-markdown) {
    padding: 16px;
    margin: 16px 0;
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 6px;
    border-left: 4px solid #409eff;
}

.markdown-content :deep(.nested-markdown h1),
.markdown-content :deep(.nested-markdown h2),
.markdown-content :deep(.nested-markdown h3),
.markdown-content :deep(.nested-markdown h4),
.markdown-content :deep(.nested-markdown h5),
.markdown-content :deep(.nested-markdown h6) {
    margin-top: 16px;
}

.markdown-content :deep(.nested-markdown h1:first-child),
.markdown-content :deep(.nested-markdown h2:first-child),
.markdown-content :deep(.nested-markdown h3:first-child),
.markdown-content :deep(.nested-markdown h4:first-child),
.markdown-content :deep(.nested-markdown h5:first-child),
.markdown-content :deep(.nested-markdown h6:first-child) {
    margin-top: 0;
}

.markdown-content :deep(.nested-markdown p:last-child),
.markdown-content :deep(.nested-markdown ul:last-child),
.markdown-content :deep(.nested-markdown ol:last-child) {
    margin-bottom: 0;
}
</style>
