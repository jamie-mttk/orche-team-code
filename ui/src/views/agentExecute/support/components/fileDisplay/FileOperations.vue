<template>
    <div class="file-operations">
        <el-button type="primary" size="small" @click="handleViewFile">
            <Icon name="mdiEye" />
            查看
        </el-button>
        <el-button type="success" size="small" @click="handleDownloadFile">
            <Icon name="mdiDownload" />
            下载
        </el-button>

        <!-- 文件查看器 -->
        <FileViewerDrawer v-model="fileViewerVisible" :fileName="fileName" :file-content="currentFileContent"
            :session-id="sessionId" :file-description="fileDescription" />
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import FileViewerDrawer from './FileViewerDrawer.vue'
import { downloadFile, getFileContent } from './fileUtils.ts'

interface Props {
    fileName: string
    fileContent?: string
    sessionId: string
    fileDescription?: string
}

const props = defineProps<Props>()

// 文件查看器状态
const fileViewerVisible = ref(false)
const currentFileContent = ref('')

// 查看文件
const handleViewFile = async () => {
    try {
        // 从服务器获取文件内容
        const content = await getFileContent(props.sessionId, props.fileName)
        currentFileContent.value = content
        fileViewerVisible.value = true
    } catch (error) {

        ElMessage.error('获取文件内容失败')
    }
}

// 下载文件
const handleDownloadFile = async () => {
    try {
        await downloadFile(props.sessionId, props.fileName)
    } catch (error) {
        // 错误已在downloadFile中处理
        ElMessage.error('下载文件失败')
    }
}
</script>

<style scoped>
.file-operations {
    display: flex;
    gap: 10px;
    align-items: center;
}
</style>
