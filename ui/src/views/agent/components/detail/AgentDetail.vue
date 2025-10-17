<template>
  <div class="agent-form-container">

    <!-- 页头 -->
    <div class="page-header">
      <el-page-header @back="handleBack" title="返回">
        <template #content>
          <span class="page-title">{{ isEdit ? '编辑智能体' : '创建智能体' }}</span>
        </template>
        <template #extra>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建智能体' }}
          </el-button>
        </template>
      </el-page-header>
    </div>
    <!-- 表单内容 -->
    <div class="form-content">
      <!-- 基本信息部分 -->
      <el-collapse v-model="activeNames">
        <el-collapse-item name="basic">
          <template #title>
            <div class="collapse-title">
              <Icon name="mdiAccount" />
              <span class="title-text">基本信息</span>
            </div>
          </template>

          <CdForm ref="basicFormRef" :config="basicConfig" :data="formData || {}" />


        </el-collapse-item>

        <!-- 智能体配置部分 -->
        <el-collapse-item name="config">
          <template #title>
            <div class="collapse-title">
              <Icon name="mdiCog" />
              <span class="title-text">智能体配置</span>
            </div>
          </template>

          <CdForm v-if="agentTempalteUiConfig.children && agentTempalteUiConfig.children.length > 0" ref="configFormRef"
            :config="agentTempalteUiConfig" :data="formData.config || {}" />
          <el-empty v-else description="此智能体不需要任何配置" :image-size="120" />

        </el-collapse-item>

        <!-- 合作智能体部分 -->
        <el-collapse-item name="members" v-if="supportMembers">
          <template #title>
            <div class="collapse-title">
              <Icon name="mdiRobot" />
              <span class="title-text">合作智能体</span>
              <el-badge :value="formData.members?.length || 0" class="member-badge" />
              <el-button type="primary" size="small" @click.stop="showAddMemberDialog = true"
                style="margin-left: 10px;">
                <Icon name="mdiPlus" />
                添加合作智能体
              </el-button>
            </div>
          </template>

          <AgentMembersManager v-model="members" :exclude-id="props.agent?._id"
            v-model:show-dialog="showAddMemberDialog" />
        </el-collapse-item>
      </el-collapse>
    </div>



  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import AgentMembersManager from './AgentMembersManager.vue'
import type { Agent } from '../../types'
import { deepCopy } from '@/utils/tools'
import CdForm from '@/components/ConfigDyna/Form.vue'
import { agentTemplateAPI } from '@/api'

// 标签类型定义
interface Tag {
  _id: string
  name: string
}

const props = defineProps<{
  agent?: Agent
  isEdit: boolean
  submitting: boolean
}>()

const emit = defineEmits<{
  submit: [data: Agent]
  back: []
}>()
//
const agentTemplateConfig = ref({})
// 表单引用
const basicFormRef = ref<FormInstance>()
const configFormRef = ref<FormInstance>()

// 折叠面板状态
const activeNames = ref(['basic', 'config', 'members'])

// 添加合作智能体对话框状态
const showAddMemberDialog = ref(false)



// 表单数据
const formData = ref<Agent>({
  config: {
  },
  members: []
})

// 计算属性 - 确保 members 始终是数组
const members = computed({
  get: () => formData.value.members || [],
  set: (value: string[]) => {
    formData.value.members = value
  }
})



//
const basicConfig = {
  props: {
    labelWidth: '100px'
  },
  "children": [
    {
      "key": "name",
      "label": "名称",
      "size": 1,
      "mandatory": true,
    },
    {
      "key": "description",
      "label": "描述",
      "size": 1,
      "mandatory": true,
      "props": {
        "type": "multiple", rows: 6
      }
    },
    {
      "key": "tags",
      "label": "标签",
      "size": 1,
      "mode": "select",
      "props": {
        "url": "/agentTag/query",
        "multiple": true
      }
    }]
}

const loadAgentConfig = async () => {
  if (!formData.value.agentTemplate) {
    return
  }
  const response = await agentTemplateAPI.getTemplateByKey(formData.value.agentTemplate)
  agentTemplateConfig.value = response.data


}

//agent template ui form
const agentTempalteUiConfig = computed(() => {
  if (agentTemplateConfig.value && agentTemplateConfig.value.ui) {
    const uiConfig = agentTemplateConfig.value.ui
    if (!uiConfig.props) {
      uiConfig.props = {}
    }
    if (!uiConfig.props.labelWidth) {
      uiConfig.props.labelWidth = '100px'
    }
    return uiConfig
  }
  return {}
})
//是否支持members
const supportMembers = computed(() => {
  if (agentTemplateConfig.value && agentTemplateConfig.value.props && Array.isArray(agentTemplateConfig.value.props)) {
    return agentTemplateConfig.value.props.includes('SUPPORT_MEMBER')
  }
  return false
})





// 初始化表单数据
const initFormData = () => {
  formData.value = deepCopy(props.agent || { config: {}, members: [] })
}



// 返回处理
const handleBack = () => {
  emit('back')
}

// 提交处理
const handleSubmit = async () => {
  try {
    // 验证基本信息表单
    await basicFormRef.value?.validate()

    // 验证配置表单
    await configFormRef.value?.validate()

    // 提交数据

    emit('submit', formData.value)
  } catch (error) {
    // 验证失败，显示错误信息
    ElMessage.error('请检查表单填写是否正确')
  }
}

// 监听 agentTemplate 变化，自动加载配置
watch(() => formData.value.agentTemplate, (newTemplate) => {
  if (newTemplate) {
    loadAgentConfig()
  }
})

// 生命周期
onMounted(() => {
  initFormData()
  loadAgentConfig()

})
</script>

<style scoped>
.agent-form-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--el-fill-color-extra-light);
}

.page-header {
  background: white;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.form-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-weight: 600;
  font-size: 16px;
}

.basic-form,
.config-form {
  max-width: 100%;
  padding: 20px 0;
}

.member-badge {
  margin-left: 10px;
}

.tags-section {
  margin-top: 20px;
  padding: 20px 0;
  border-top: 1px solid var(--el-border-color-light);
}

.tags-section h4 {
  margin: 0 0 15px 0;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.tag-checkbox {
  display: inline-block;
  margin-right: 16px;
  margin-bottom: 8px;
}

.no-tags-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  padding: 10px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  margin-top: 10px;
}



/* 响应式布局 */
@media (max-width: 768px) {
  .page-header {
    padding: 12px 16px;
  }

  .form-content {
    padding: 16px;
  }

  .basic-form,
  .config-form {
    padding: 15px 0;
  }

  .members-section {
    padding: 15px 0;
  }
}
</style>
