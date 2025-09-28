<template>
  <div class="agent-management-page">
    <!-- 智能体列表界面 -->
    <AgentList v-if="!showForm" :agents="agents" :search-name="searchName" @add="showTemplateSelector = true"
      @edit="showEditForm" @delete="handleDelete" @execute="handleExecute" @update:search-name="searchName = $event" />

    <!-- 智能体模板选择界面 -->
    <AgentTemplateSelector v-model="showTemplateSelector" @select="handleTemplateSelect" />

    <!-- 新增/修改界面 -->
    <AgentDetail v-if="showForm" :agent="currentAgent" :is-edit="isEdit" :submitting="submitting" @submit="handleSubmit"
      @back="backToList" />


  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { agentAPI } from '@/api'

import AgentList from './components/list/AgentList.vue'
import AgentDetail from './components/detail/AgentDetail.vue'
import AgentTemplateSelector from './components/AgentTemplateSelector.vue'
import type { Agent, AgentTemplate } from './types'

// Router实例
const router = useRouter()

// 界面状态
const showForm = ref(false)
const showTemplateSelector = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentAgent = ref<Agent | undefined>()

// 搜索
const searchName = ref('')

// 智能体列表
const agents = ref<Agent[]>([])

// 加载智能体列表
const loadAgents = async () => {
  try {
    const response = await agentAPI.searchAgents()
    agents.value = response.data.list || []
  } catch (error) {
    console.error('加载智能体列表失败:', error)
    ElMessage.error('加载智能体列表失败')
    agents.value = []
  }
}



// 显示修改表单
const showEditForm = async (agent: Agent) => {
  try {
    // 获取完整的智能体信息
    if (agent._id) {
      const response = await agentAPI.getAgent(agent._id)
      currentAgent.value = response.data
    } else {
      currentAgent.value = agent
    }

    isEdit.value = true
    showForm.value = true
  } catch (error) {
    console.error('获取智能体详情失败:', error)
    ElMessage.error('获取智能体详情失败')
  }
}

// 处理模板选择
const handleTemplateSelect = (template: AgentTemplate) => {

  // 根据选择的模板创建空的智能体
  currentAgent.value = {
    name: template.name,
    description: template.description,
    agentTemplate: template.key,
    config: {

    },
    members: []
  }

  isEdit.value = false
  showForm.value = true
}

// 返回列表
const backToList = () => {
  showForm.value = false
  showTemplateSelector.value = false
  isEdit.value = false
  currentAgent.value = undefined
}

// 提交表单
const handleSubmit = async (formData: Agent) => {
  try {
    submitting.value = true

    if (isEdit.value) {
      await agentAPI.saveAgent(formData)

    } else {
      // 新增智能体
      await agentAPI.saveAgent(formData)

    }
    ElMessage.success('保存成功')
    backToList()
    loadAgents() // 刷新列表
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

// 删除智能体
const handleDelete = async (agent: Agent) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除智能体 "${agent.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (agent._id) {
      await agentAPI.deleteAgent(agent._id)
    }
    ElMessage.success('删除成功')
    loadAgents() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 执行智能体
const handleExecute = async (agent: Agent) => {
  try {
    if (!agent._id) {
      ElMessage.error('智能体ID不存在')
      return
    }

    // 在新浏览器窗口打开执行页面
    // 使用router.resolve生成完整的URL，然后在新窗口打开
    const resolved = router.resolve({
      name: 'AgentExecute',
      params: { agentId: agent._id }
    })
    window.open(resolved.href, '_blank')
  } catch (error) {
    console.error('执行智能体失败:', error)
    ElMessage.error('执行智能体失败')
  }
}



// 页面加载时获取智能体列表
onMounted(() => {
  loadAgents()
})
</script>

<style scoped>
.agent-management-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: var(--el-fill-color-extra-light);
}
</style>
