<template>
    <div class="default-display">
        <el-card class="display-card">
            <template #header>
                <div class="card-header">
                    <span class="title">数据内容</span>
                </div>
            </template>

            <div class="content-container">
                <div v-if="!hasAnyData" class="no-data">
                    <el-empty description="暂无数据" />
                </div>
                <div v-else class="data-list">
                    <!-- 内容数据 -->
                    <el-collapse v-if="data?.contents && data.contents.length > 0" v-model="activeCollapse"
                        class="data-collapse">
                        <el-collapse-item v-for="(item, index) in data.contents" :key="`content-${index}`"
                            :name="`content-${index}`">
                            <template #title>
                                <div class="collapse-title">
                                    <span class="title-text">📝 数据项 {{ index + 1 }}</span>
                                    <el-button type="primary" plain size="small" @click.stop="copyToClipboard(item)"
                                        class="copy-button" title="复制内容">
                                        <icon name="mdiContentCopy" size="small" />
                                    </el-button>
                                </div>
                            </template>
                            <div class="collapse-content">
                                <MarkdownViewer :key="`content-${index}-${Date.now()}`" :value="item" />
                            </div>
                        </el-collapse-item>
                    </el-collapse>

                    <!-- 文件数据 -->
                    <el-collapse v-if="data?.files && data.files.length > 0" v-model="activeCollapse"
                        class="data-collapse">
                        <el-collapse-item name="files">
                            <template #title>
                                <span class="collapse-title">
                                    <span>📁 文件数据</span>
                                    <el-tag size="small" type="success">{{ data.files.length }} 项</el-tag>
                                </span>
                            </template>
                            <div class="collapse-content">
                                <FileDisplay :files="data.files" :session-id="props.agentRuntime.sessionId.value" />
                            </div>
                        </el-collapse-item>
                    </el-collapse>

                    <!-- 错误信息 -->
                    <el-collapse v-if="data?.errors && data.errors.length > 0" v-model="activeCollapse"
                        class="data-collapse">
                        <el-collapse-item name="errors">
                            <template #title>
                                <span class="collapse-title">
                                    <span>❌ 错误信息</span>
                                    <el-tag size="small" type="danger">{{ data.errors.length }} 项</el-tag>
                                </span>
                            </template>
                            <div class="collapse-content">
                                <div v-for="(item, index) in data.errors" :key="`error-${index}`"
                                    class="data-item error-item">
                                    <div class="item-header">
                                        <span class="item-title">
                                            <span>❌ 错误 {{ index + 1 }}</span>
                                        </span>
                                    </div>
                                    <div class="item-content">
                                        <MarkdownViewer :key="`error-${index}-${Date.now()}`" :value="item" />
                                    </div>
                                </div>
                            </div>
                        </el-collapse-item>
                    </el-collapse>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import MarkdownViewer from '@/components/mdViewer/index.vue'
import FileDisplay from '../../components/fileDisplay/index.vue'
import Icon from '@/components/mdiIicon/index.vue'
import type { AgentRuntime } from '../../agentRuntimeSupport'

interface Props {
    data: any
    agentRuntime: AgentRuntime
}

const props = defineProps<Props>()

// 控制折叠面板的展开状态
const activeCollapse = ref<string[]>([])

// 计算所有折叠面板的name，用于默认展开
const allCollapseNames = computed(() => {
    const names: string[] = []

    // 添加内容数据项
    if (props.data?.contents?.length > 0) {
        props.data.contents.forEach((_: any, index: number) => {
            names.push(`content-${index}`)
        })
    }

    // 添加文件数据
    if (props.data?.files?.length > 0) {
        names.push('files')
    }

    // 添加错误信息
    if (props.data?.errors?.length > 0) {
        names.push('errors')
    }

    return names
})

// 监听数据变化，自动展开所有面板
watch(() => allCollapseNames.value, (newNames) => {
    activeCollapse.value = [...newNames]
}, { immediate: true })

// 判断是否有任何数据
const hasAnyData = computed(() => {
    return (props.data?.contents?.length > 0) ||
        (props.data?.files?.length > 0) ||
        (props.data?.errors?.length > 0)
})

// 拷贝到剪贴板
const copyToClipboard = async (content: string) => {
    try {
        await navigator.clipboard.writeText(content)
        ElMessage.success('内容已复制到剪贴板')
    } catch (err) {
        console.error('复制失败:', err)
        ElMessage.error('复制失败')
    }
}
</script>

<style scoped>
.default-display {
    height: 100%;
}

.display-card {
    height: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.title {
    font-weight: bold;
    font-size: 16px;
}

.content-container {

    overflow-y: auto;
}

.no-data {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.data-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.data-collapse {
    border: none;
}

.collapse-title {
    display: flex;
    align-items: center;
    width: 100%;
    font-weight: 600;
    font-size: 14px;
    gap: 8px;
}

.title-text {
    /* flex: 1; */
}

.collapse-content {
    padding: 10px 0;
}

.data-item {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    overflow: hidden;
}

.item-header {
    background-color: #f5f7fa;
    padding: 10px 15px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.item-title {
    font-weight: 600;
    color: #303133;
    font-size: 14px;
}

.item-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.item-content {
    padding: 10px;
}

.error-item {
    border-color: #f56c6c;
}

.error-item .item-header {
    background-color: #fef0f0;
    border-bottom-color: #f56c6c;
}

.error-item .item-title {
    color: #f56c6c;
}

.copy-button {
    width: 24px;
    height: 24px;
    padding: 0;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 24px;
    min-height: 24px;
}

.copy-button .mdi-icon {
    width: 14px;
    height: 14px;
}
</style>
