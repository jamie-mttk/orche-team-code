<template>
  <div class="agent-list-view">
    <!-- 页面头部 -->
    <PageHeader title="智能体列表" icon="robot" :show-add-button="true" add-button-text="新增智能体" @add="$emit('add')" />

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：智能体列表 (80%) -->
      <div class="left-panel">
        <!-- 智能体卡片列表 -->
        <el-row :gutter="20" class="agent-cards">
          <el-col v-for="agent in filteredAgents" :key="agent._id || ''" :span="4" :xs="12" :sm="8" :md="6" :lg="6"
            :xl="4">
            <AgentCard :agent="agent as Agent & { _id: string }" :allTags="allTags" @edit="$emit('edit', $event)"
              @delete="$emit('delete', $event)" @execute="$emit('execute', $event)" />
          </el-col>
        </el-row>

        <!-- 空状态 -->
        <el-empty v-if="filteredAgents.length === 0" description="暂无智能体数据" class="empty-state" />
      </div>

      <!-- 右侧：过滤面板 (20%) -->
      <div class="right-panel">
        <el-card class="filter-panel">
          <template #header>
            <div class="filter-header">
              <div class="filter-title">
                <Icon name="mdiFilter" />
                <span>过滤条件</span>
              </div>
              <el-button @click="clearFilters" type="info" size="small" plain>

                清除所有过滤
              </el-button>
            </div>
          </template>

          <!-- 按名称搜索 -->
          <div class="filter-section">
            <h4>按名称搜索</h4>
            <el-input v-model="searchName" placeholder="按名称搜索智能体" clearable style="margin-top: 6px;">
              <template #prefix>
                <Icon name="mdiMagnify" />
              </template>
            </el-input>
          </div>

          <!-- 标签过滤 -->
          <div class="filter-section">
            <h4>标签过滤</h4>
            <div class="tag-list">
              <el-checkbox-group v-model="selectedTags" @change="handleTagChange">
                <el-checkbox v-for="tag in allTags" :key="tag._id" :label="tag._id" class="tag-checkbox">
                  {{ tag.name }}
                </el-checkbox>
              </el-checkbox-group>
              <div v-if="allTags.length === 0" class="no-tags-tip">
                <Icon name="mdiInformation" />
                <span>暂无标签数据</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import AgentCard from './AgentCard.vue'
import Icon from '@/components/mdiIicon/index.vue'
import PageHeader from '@/components/pageHeader/index.vue'
import { agentTagAPI } from '@/api'

import type { Agent } from '../../types'

// 标签类型定义
interface Tag {
  _id: string
  name: string
}

interface Props {
  agents: Agent[]
}

interface Emits {
  (e: 'add'): void
  (e: 'edit', agent: Agent): void
  (e: 'delete', agent: Agent): void
  (e: 'execute', agent: Agent): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

// 响应式数据
const searchName = ref('')
const selectedTags = ref<string[]>([])
const allTags = ref<Tag[]>([])

// 从localStorage加载用户选择
const loadUserPreferences = () => {
  try {
    const savedSearchName = localStorage.getItem('agentList_searchName')
    const savedSelectedTags = localStorage.getItem('agentList_selectedTags')

    if (savedSearchName) {
      searchName.value = savedSearchName
    }
    if (savedSelectedTags) {
      selectedTags.value = JSON.parse(savedSelectedTags)
    }
  } catch (error) {
    console.error('加载用户偏好设置失败:', error)
  }
}

// 保存用户选择到localStorage
const saveUserPreferences = () => {
  try {
    localStorage.setItem('agentList_searchName', searchName.value)
    localStorage.setItem('agentList_selectedTags', JSON.stringify(selectedTags.value))
  } catch (error) {
    console.error('保存用户偏好设置失败:', error)
  }
}

// 加载所有标签
const loadAllTags = async () => {
  try {
    const response = await agentTagAPI.queryTags()
    if (response.data) {
      allTags.value = response.data.list

    }
  } catch (error) {
    console.error('加载标签失败:', error)
    ElMessage.error('加载标签失败')
  }
}

// 过滤后的智能体列表
const filteredAgents = computed(() => {
  let filtered = props.agents

  // 按名称过滤
  if (searchName.value) {
    filtered = filtered.filter(agent =>
      agent.name?.toLowerCase().includes(searchName.value.toLowerCase())
    )
  }

  // 按标签过滤
  if (selectedTags.value.length > 0) {
    filtered = filtered.filter(agent => {
      // 如果agent没有tags字段，则不匹配任何选中的标签
      if (!agent.tags || !Array.isArray(agent.tags)) {
        return false
      }
      // 检查agent的tags是否包含任何选中的标签
      return selectedTags.value.some(tagId => agent.tags!.includes(tagId))
    })
  }

  return filtered
})

// 处理标签变化
const handleTagChange = () => {
  saveUserPreferences()
}

// 清除所有过滤条件
const clearFilters = () => {
  searchName.value = ''
  selectedTags.value = []
  saveUserPreferences()
}

// 监听搜索名称变化
watch(searchName, () => {
  saveUserPreferences()
})

// 生命周期
onMounted(() => {
  loadAllTags()
  loadUserPreferences()
})
</script>

<style scoped>
.agent-list-view {
  flex: 1;
  display: flex;
  flex-direction: column;
}


.main-content {
  flex: 1;
  display: flex;
  gap: 20px;
  overflow: hidden;
}

.left-panel {
  flex: 1;
  overflow-y: auto;
}

.right-panel {
  flex: 0 0 320px;
  min-width: 0;
}

.agent-cards {
  overflow-y: auto;
  margin: 0px !important;
  /*防止下面出现滚动条*/
}

.empty-state {
  margin: 40px 0;
}

.filter-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-section h4 {
  margin: 0 0 10px 0;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.tag-list {
  margin-top: 16px;
  max-height: 200px;
  overflow-y: auto;
}

.tag-checkbox {
  display: block;
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
  .main-content {
    flex-direction: column;
    gap: 15px;
  }

  .left-panel,
  .right-panel {
    flex: none;
  }

  .right-panel {
    order: -1;
  }
}
</style>
