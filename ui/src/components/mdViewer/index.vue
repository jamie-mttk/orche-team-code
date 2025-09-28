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
const detectContentType = (content: string): 'html' | 'markdown' => {
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
        // 如果是Markdown，使用marked处理
        return marked(value)
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
    margin-top: 24px;
    margin-bottom: 16px;
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
    margin-bottom: 16px;
    color: #303133;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
    margin-bottom: 16px;
    padding-left: 24px;
}

.markdown-content :deep(li) {
    margin-bottom: 8px;
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
    margin: 16px 0;
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
</style>
