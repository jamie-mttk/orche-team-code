<template>
  <el-dialog v-model="modelValue" title="选择合作智能体" width="60%" :close-on-click-modal="false" :append-to-body="true"
    :close-on-press-escape="false">
    <template #header>
      <div class="dialog-header">
        <h3>选择合作智能体</h3>
        <div class="search-container">
          <el-input v-model="searchName" placeholder="搜索智能体名称" clearable @input="handleSearch" style="width: 300px">
            <template #prefix>
              <Icon name="mdiMagnify" />
            </template>
          </el-input>
        </div>
      </div>
    </template>

    <div class="member-selector-content">


      <el-row :gutter="16" class="member-grid">
        <el-col v-for="agent in availableAgents" :key="agent._id" :span="4" :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
          <AgentCardItem :agent="agent" action-type="add" @action="addMember" />
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="availableAgents.length === 0" description="暂无可用智能体" :image-size="100" />
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import AgentCardItem from '../list/AgentCardItem.vue'
import { agentAPI } from '@/api'
import type { Agent } from '../../types'

const props = defineProps<{
  modelValue: boolean
  selectedMembers: string[]
  excludeId?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'add': [agentId: string]
}>()

// 使用 defineModel
const modelValue = defineModel<boolean>()

// 响应式数据
const allAgents = ref<Agent[]>([])
const searchName = ref('')
const loading = ref(false)

// 计算属性
const availableAgents = computed(() => {
  let filtered = allAgents.value.filter(agent => {
    // 排除当前智能体自身
    if (props.excludeId && agent._id === props.excludeId) {
      return false
    }
    // 排除已选择的智能体
    if (props.selectedMembers.includes(agent._id || '')) {
      return false
    }
    // 按名称过滤
    if (searchName.value) {
      return agent.name?.toLowerCase().includes(searchName.value.toLowerCase()) || false
    }
    return true
  })
  return filtered
})

// 加载智能体列表
const loadAgents = async () => {
  try {
    loading.value = true
    const response = await agentAPI.searchAgents()
    allAgents.value = response.data.list || []
  } catch (error) {
    console.error('加载智能体列表失败:', error)
    ElMessage.error('加载智能体列表失败')
    allAgents.value = []
  } finally {
    loading.value = false
  }
}

// 添加成员
const addMember = (agent: Agent) => {
  if (!agent._id) return
  emit('add', agent._id)
  // 添加后不关闭对话框，允许继续选择
  // 清空搜索框，方便继续选择
  searchName.value = ''
}

// 搜索处理
const handleSearch = () => {
  // 搜索时不需要特殊处理，computed会自动过滤
}

// 取消选择
const handleCancel = () => {
  modelValue.value = false
  searchName.value = ''
}

// 监听对话框打开
const handleDialogOpen = () => {
  if (modelValue.value) {
    loadAgents()
  }
}

// 监听 modelValue 变化
watch(() => modelValue.value, handleDialogOpen)
</script>

<style scoped>
/* 对话框样式 */
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

.member-selector-content {
  min-height: 400px;
  padding: 20px;
}

.selection-tip {
  margin-bottom: 20px;
}

.member-grid {
  margin-bottom: 20px;
  max-height: 500px;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 2px;
  border: 1px solid transparent;
}



.dialog-footer {
  text-align: right;
}
</style>
