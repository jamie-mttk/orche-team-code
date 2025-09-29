<template>

    <CdBase :config="props.config" :data="props.data" v-if="!isDisabled && modelValue || modelValue.length > 0">
        <div class="files-container">
            <!-- 文件列表 -->
            <el-table v-if="modelValue && modelValue.length > 0" :data="modelValue"
                style="width: 100%; margin-bottom: 16px;">
                <el-table-column prop="name" label="文件名" min-width="200">
                    <template #default="{ row }">
                        <div class="file-name-cell">
                            <Icon name="mdiFile" size="small" color="#409eff" />
                            <span>{{ row.name }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="size" label="大小" width="100">
                    <template #default="{ row }">
                        {{ formatFileSize(row.size) }}
                    </template>
                </el-table-column>
                <el-table-column label="描述" min-width="200">
                    <template #default="{ row }">
                        <el-input v-model="row.description" placeholder="添加描述..." size="small" :readonly="isDisabled"
                            @blur="updateDescription(row)" />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                    <template #default="{ row }">
                        <el-button type="danger" size="small" @click="removeFile(row.id)">
                            <Icon name="mdiDelete" size="small" />
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 上传区域 -->
            <div v-if="!isDisabled" ref="uploadArea" class="upload-area" :class="{ 'drag-over': isDragOver }"
                @click="triggerFileInput" @dragover.prevent="handleDragOver" @dragleave.prevent="handleDragLeave"
                @drop.prevent="handleDrop">
                <div class="upload-content">
                    <Icon name="mdiUpload" size="medium" color="#c0c4cc" />
                    <span class="upload-text">点击或拖拽单个文件到此处上传</span>
                </div>
            </div>

            <!-- 隐藏的文件输入 -->
            <input ref="fileInput" type="file" style="display: none" @change="handleFileSelect" />
        </div>
    </CdBase>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import http from '@/utils/request'

import type { BaseProps } from './support'
import { useSupport } from './support'
import CdBase from './Base.vue'
import { formatFileSize } from '@/utils/tools'

const props = defineProps<BaseProps>()
const { modelValue, isDisabled } = useSupport(props)
if (modelValue.value == undefined) {
    modelValue.value = []
}


// 响应式数据
const uploadArea = ref<HTMLElement>()
const fileInput = ref<HTMLInputElement>()
const isDragOver = ref(false)





// 触发文件选择
const triggerFileInput = () => {
    fileInput.value?.click()
}

// 处理文件选择
const handleFileSelect = (event: Event) => {
    const target = event.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
        // 只取第一个文件
        uploadSingleFile(target.files[0])
        // 清空input，允许重复选择同一文件
        target.value = ''
    }
}

// 处理拖拽进入
const handleDragOver = (event: DragEvent) => {
    event.preventDefault()
    isDragOver.value = true
}

// 处理拖拽离开
const handleDragLeave = (event: DragEvent) => {
    event.preventDefault()
    isDragOver.value = false
}

// 处理文件拖拽放置
const handleDrop = (event: DragEvent) => {
    event.preventDefault()
    isDragOver.value = false

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
        // 只取第一个文件
        uploadSingleFile(event.dataTransfer.files[0])
    }
}

// 上传单个文件并添加到列表
const uploadSingleFile = async (file: File) => {
    try {
        const result = await uploadFileToServer(file)
        const newFile = {
            id: result.id,
            name: result.name,
            size: result.size,
            description: ''
        }

        // 添加到现有文件列表
        modelValue.value = [...modelValue.value, newFile]
        ElMessage.success(`成功上传文件: ${file.name}`)
    } catch (error) {
        console.error('文件上传失败:', error)
        ElMessage.error(`文件上传失败: ${file.name}`)
    }
}

// 上传文件到服务器
const uploadFileToServer = async (file: File): Promise<{ id: string; name: string; size: number }> => {
    const formData = new FormData()
    formData.append('file', file)

    const response = await http.post('/chat/uploadFile', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 300000 // 文件上传超时时间设置为300秒
    })

    return response.data
}

// 更新文件描述
const updateDescription = (file) => {
    // 触发更新，让父组件知道描述已修改
    const updatedFiles = modelValue.value.map(f =>
        f.id === file.id ? { ...f, description: file.description } : f
    )
    modelValue.value = updatedFiles
}

// 删除文件
const removeFile = (fileId: string) => {
    modelValue.value = modelValue.value.filter(file => file.id !== fileId)
    ElMessage.success('文件已删除')
}
</script>

<style scoped>
.files-container {
    width: 100%;
}

.file-name-cell {
    display: flex;
    align-items: center;
    gap: 8px;
}

.upload-area {
    border: 2px dashed #d9d9d9;
    border-radius: 6px;
    padding: 12px 20px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    background-color: #fafafa;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.upload-area:hover {
    border-color: #409eff;
    background-color: #f0f9ff;
}

.upload-area.drag-over {
    border-color: #409eff;
    background-color: #e6f7ff;
}

.upload-content {
    display: flex;
    align-items: center;
    gap: 8px;
}

.upload-text {
    color: #606266;
    font-size: 14px;
}
</style>