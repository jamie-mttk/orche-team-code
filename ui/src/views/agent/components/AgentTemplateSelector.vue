<template>
    <el-dialog v-model="modelValue" width="60%" :close-on-click-modal="false" :close-on-press-escape="false">
        <!-- 搜索框在header slot中 -->
        <template #header>
            <div class="dialog-header">
                <h3>选择智能体模板</h3>
                <div class="search-container">
                    <el-input v-model="searchName" placeholder="搜索模板名称" clearable @input="handleSearch"
                        style="width: 300px">
                        <template #prefix>
                            <Icon name="magnify" />
                        </template>
                    </el-input>
                </div>
            </div>
        </template>

        <div class="template-selector">
            <!-- 模板列表 -->
            <div class="template-list">
                <el-row :gutter="20">
                    <el-col v-for="template in filteredTemplates" :key="template._id" :span="8"
                        style="margin-bottom: 20px">
                        <el-card class="template-card" :class="{ 'selected': selectedTemplate?._id === template._id }"
                            @click="selectTemplate(template)" hoverable>
                            <div class="template-content">
                                <h3 class="template-name">{{ template.name }}</h3>
                                <p class="template-description">{{ template.description || '暂无描述' }}</p>
                                <div class="template-key">
                                    <el-tag size="small" type="info">{{ template.key }}</el-tag>
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>

                <!-- 空状态 -->
                <el-empty v-if="filteredTemplates.length === 0" description="暂无模板" :image-size="100" />
            </div>
        </div>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" :disabled="!selectedTemplate" @click="handleConfirm">
                    确定
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import { agentTemplateAPI } from '@/api'
import type { AgentTemplate } from '../types'

defineProps<{
    modelValue: boolean
}>()

const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'select': [template: AgentTemplate]
}>()

// 使用defineModelValue
const modelValue = defineModel<boolean>()

const templates = ref<AgentTemplate[]>([])
const searchName = ref('')
const selectedTemplate = ref<AgentTemplate | null>(null)
const loading = ref(false)

// 过滤后的模板列表
const filteredTemplates = computed(() => {
    if (!searchName.value) {
        return templates.value
    }
    return templates.value.filter(template =>
        template.name.toLowerCase().includes(searchName.value.toLowerCase())
    )
})

// 加载模板列表
const loadTemplates = async () => {
    try {
        loading.value = true
        const response = await agentTemplateAPI.searchTemplates()
        templates.value = response.data.list || []
    } catch (error) {
        console.error('加载智能体模板失败:', error)
        ElMessage.error('加载智能体模板失败')
        templates.value = []
    } finally {
        loading.value = false
    }
}

// 搜索处理
const handleSearch = () => {
    // 搜索时清空选择
    selectedTemplate.value = null
}

// 选择模板
const selectTemplate = (template: AgentTemplate) => {
    selectedTemplate.value = template
}

// 确认选择
const handleConfirm = () => {
    if (selectedTemplate.value) {
        emit('select', selectedTemplate.value)
        modelValue.value = false
        selectedTemplate.value = null
        searchName.value = ''
    }
}

// 取消选择
const handleCancel = () => {
    modelValue.value = false
    selectedTemplate.value = null
    searchName.value = ''
}

// 监听对话框打开
const handleDialogOpen = () => {
    if (modelValue.value) {
        loadTemplates()
    }
}

// 监听modelValue变化
watch(() => modelValue.value, handleDialogOpen)
</script>

<style scoped>
.dialog-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 100%;
}

.dialog-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.search-container {
    display: flex;
    justify-content: center;
    flex: 1;
}

.template-selector {
    min-height: 400px;
}

.template-list {
    max-height: 500px;
    overflow-y: auto;
    overflow-x: hidden;
    /**Cursor禁止删除 */
}

.template-card {
    height: 120px;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;
}

.template-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.template-card.selected {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
}

.template-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.template-name {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.template-description {
    margin: 0 0 12px 0;
    font-size: 14px;
    color: var(--el-text-color-regular);
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.template-key {
    text-align: right;
}

.dialog-footer {
    text-align: right;
}
</style>
