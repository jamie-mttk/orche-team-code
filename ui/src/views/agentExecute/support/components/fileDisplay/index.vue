<template>
    <div class="file-display">
        <div v-if="files.length === 0" class="no-files">
            <el-empty description="暂无文件" />
        </div>

        <div v-else class="files-list">
            <el-card v-for="(file, index) in files" :key="index" class="file-item" shadow="hover">
                <div class="file-header">
                    <div class="file-icon">
                        <Icon name="mdiFile" size="medium" />
                    </div>
                    <div class="file-info">
                        <div class="file-name" :title="file.fileName">
                            {{ file.fileName }}
                        </div>
                        <div class="file-description" :title="file.description">
                            {{ file.description }}
                        </div>
                    </div>
                </div>

                <div class="file-actions">
                    <FileOperations :fileName="file.fileName" :file-content="file.fileContent" :session-id="sessionId"
                        :file-description="file.description" />
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import Icon from '@/components/mdiIicon/index.vue'
import FileOperations from './FileOperations.vue'

interface FileInfo {
    fileName: string
    description: string
    fileContent?: string
}

interface Props {
    files: FileInfo[]
    sessionId: string
}

defineProps<Props>()
</script>

<style scoped>
.file-display {
    margin-bottom: 20px;
}

.no-files {
    margin: 20px 0;
}

.files-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.file-item {
    border: 1px solid #e4e7ed;
    transition: all 0.3s ease;
}

.file-item:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.file-header {
    display: flex;
    align-items: flex-start;
    gap: 15px;
    margin-bottom: 15px;
}

.file-icon {
    color: #409eff;
    margin-top: 2px;
}

.file-info {
    flex: 1;
    min-width: 0;
}

.file-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    line-height: 1.4;
    word-break: break-all;
}

.file-description {
    color: #606266;
    font-size: 14px;
    line-height: 1.5;
    word-break: break-all;
}

.file-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .file-header {
        flex-direction: column;
        gap: 10px;
    }

    .file-actions {
        justify-content: center;
    }
}
</style>
