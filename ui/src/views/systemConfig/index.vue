<template>
    <div class="system-config-page">
        <!-- 页面头部 -->
        <PageHeader title="系统配置" icon="cog" />

        <!-- 配置内容区域 -->
        <div class="config-content">
            <!-- HTTP配置卡片 -->
            <el-card class="config-card" shadow="hover">
                <template #header>
                    <div class="card-header">
                        <Icon name="mdiGlobe" size="medium" class="card-icon" />
                        <span class="card-title">HTTP配置</span>
                        <el-button type="primary" size="small" @click="saveHttpConfig" :loading="httpSaving">
                            <Icon name="mdiContentSave" />
                            保存
                        </el-button>
                    </div>
                </template>

                <el-form :model="httpConfig" label-width="100px" class="config-form">
                    <el-form-item label="HTTP端口">
                        <el-input-number v-model="httpConfig.httpPort" :min="1" :max="65535" placeholder="请输入HTTP端口"
                            style="width: 200px" />
                        <span class="form-tip">端口范围:1-65535</span>
                    </el-form-item>

                </el-form>
            </el-card>

            <!-- 智能体标签管理卡片 -->
            <el-card class="config-card" shadow="hover">
                <template #header>
                    <div class="card-header">
                        <Icon name="mdiTag" size="medium" class="card-icon" />
                        <span class="card-title">智能体标签</span>
                        <el-button type="primary" size="small" @click="showAddTagDialog">
                            <Icon name="mdiPlus" />
                            新增
                        </el-button>
                    </div>
                </template>

                <!-- 标签列表 -->
                <div class="tag-list">
                    <div v-if="agentTags.length === 0" class="empty-state">
                        <Icon name="mdiTag" size="large" class="empty-icon" />
                        <p>暂无标签，点击上方按钮添加标签</p>
                    </div>

                    <div v-else class="tag-items">
                        <div v-for="tag in agentTags" :key="tag._id" class="tag-item">
                            <div class="tag-content">

                                <span class="tag-name">{{ tag.name }}</span>
                            </div>
                            <div class="tag-actions">
                                <el-button size="small" plain @click="editTag(tag)">
                                    <Icon name="mdiPencil" size="small" />
                                </el-button>
                                <el-button size="small" type="danger" plain @click="deleteTag(tag)">
                                    <Icon name="mdiDelete" size="small" />
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 新增/编辑标签对话框 -->
        <el-dialog v-model="tagDialogVisible" :title="isEditTag ? '编辑标签' : '新增标签'" width="400px"
            :close-on-click-modal="false">
            <el-form :model="tagForm" :rules="tagRules" ref="tagFormRef" label-width="80px">
                <el-form-item label="标签名称" prop="name">
                    <el-input v-model="tagForm.name" placeholder="请输入标签名称" maxlength="20" show-word-limit />
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button @click="tagDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveTag" :loading="tagSaving">
                    {{ isEditTag ? '更新' : '保存' }}
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { systemConfigAPI, agentTagAPI } from '@/api'
import PageHeader from '@/components/pageHeader/index.vue'
import Icon from '@/components/mdiIicon/index.vue'

// HTTP配置
const httpConfig = reactive({
    _id: '',
    httpPort: 7474,
    _insertTime: '',
    _updateTime: ''
})

const httpSaving = ref(false)

// 智能体标签
const agentTags = ref<Array<{ _id: string; name: string }>>([])
const tagDialogVisible = ref(false)
const isEditTag = ref(false)
const tagSaving = ref(false)
const tagFormRef = ref()

const tagForm = reactive({
    _id: '',
    name: ''
} as { _id: string; name: string })

const tagRules = {
    name: [
        { required: true, message: '请输入标签名称', trigger: 'blur' },
        { min: 1, max: 20, message: '标签名称长度在1到20个字符', trigger: 'blur' }
    ]
}

// 加载HTTP配置
const loadHttpConfig = async () => {
    try {
        const response = await systemConfigAPI.getSystemConfig()
        const config = response.data
        Object.assign(httpConfig, config)
    } catch (error) {
        console.error('加载HTTP配置失败:', error)
        ElMessage.error('加载HTTP配置失败')
    }
}

// 保存HTTP配置
const saveHttpConfig = async () => {
    try {
        httpSaving.value = true
        await systemConfigAPI.saveSystemConfig(httpConfig)
        ElMessage.success('HTTP配置保存成功')
    } catch (error) {
        console.error('保存HTTP配置失败:', error)
        ElMessage.error('保存HTTP配置失败')
    } finally {
        httpSaving.value = false
    }
}

// 加载智能体标签
const loadAgentTags = async () => {
    try {
        const response = await agentTagAPI.queryTags()
        agentTags.value = response.data.list || []
    } catch (error) {
        console.error('加载智能体标签失败:', error)
        ElMessage.error('加载智能体标签失败')
        agentTags.value = []
    }
}

// 显示新增标签对话框
const showAddTagDialog = () => {
    isEditTag.value = false
    tagForm._id = ''
    tagForm.name = ''
    tagDialogVisible.value = true
}

// 编辑标签
const editTag = (tag: { _id: string; name: string }) => {
    isEditTag.value = true
    tagForm._id = tag._id
    tagForm.name = tag.name
    tagDialogVisible.value = true
}

// 保存标签
const saveTag = async () => {
    try {
        await tagFormRef.value.validate()
        tagSaving.value = true

        const tagData: { name: string; _id?: string } = {
            name: tagForm.name
        }

        if (isEditTag.value) {
            tagData._id = tagForm._id
        }

        await agentTagAPI.saveTag(tagData)
        ElMessage.success(isEditTag.value ? '标签更新成功' : '标签添加成功')
        tagDialogVisible.value = false
        loadAgentTags()
    } catch (error) {
        if (error !== false) { // 验证失败时error为false
            console.error('保存标签失败:', error)
            ElMessage.error('保存标签失败')
        }
    } finally {
        tagSaving.value = false
    }
}

// 删除标签
const deleteTag = async (tag: { _id: string; name: string }) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除标签 "${tag.name}" 吗？`,
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )

        await agentTagAPI.deleteTag(tag._id)
        ElMessage.success('标签删除成功')
        loadAgentTags()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除标签失败:', error)
            ElMessage.error('删除标签失败')
        }
    }
}

// 页面加载时获取数据
onMounted(() => {
    loadHttpConfig()
    loadAgentTags()
})
</script>

<style scoped>
.system-config-page {
    height: 100vh;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: var(--el-fill-color-extra-light);
}

.config-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
    overflow-y: auto;
}

.config-card {
    flex-shrink: 0;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
}

.card-icon {
    color: var(--el-color-primary);
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    flex: 1;
}

.config-form {
    max-width: 500px;
}

.form-tip {
    margin-left: 12px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
}

.tag-list {
    min-height: 200px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: var(--el-text-color-secondary);
}

.empty-icon {
    margin-bottom: 12px;
    opacity: 0.5;
}

.tag-items {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.tag-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: var(--el-fill-color-light);
    border-radius: var(--el-border-radius-base);
    border: 1px solid var(--el-border-color-lighter);
    transition: all 0.3s ease;
}

.tag-item:hover {
    background: var(--el-fill-color);
    border-color: var(--el-color-primary-light-7);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tag-content {
    display: flex;
    align-items: center;
    gap: 8px;
}

.tag-icon {
    color: var(--el-color-primary);
}

.tag-name {
    font-weight: 500;
    color: var(--el-text-color-primary);
}

.tag-actions {
    display: flex;
    gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .system-config-page {
        padding: 12px;
    }

    .config-content {
        gap: 16px;
    }

    .tag-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }

    .tag-actions {
        width: 100%;
        justify-content: flex-end;
    }
}
</style>
