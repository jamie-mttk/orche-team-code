<template>
  <div class="agent-card">
    <div class="card-header">
      <div class="agent-info">
        <h3 class="agent-name">{{ agent.name }}</h3>
        <div class="agent-meta">
          <span class="meta-item">
            <Icon name="mdiClock" />
            {{ formatDate((agent as any)._insertTime || (agent as any)._updateTime) }}
          </span>
        </div>
      </div>
      <div class="card-actions">
        <el-button type="primary" size="small" circle @click.stop="handleEdit">
          <Icon name="mdiPencil" />
        </el-button>
        <el-button type="success" size="small" circle @click.stop="handleExecute">
          <Icon name="mdiPlay" />
        </el-button>
        <el-button type="danger" size="small" circle @click.stop="handleDelete">
          <Icon name="mdiDelete" />
        </el-button>
      </div>
    </div>

    <div class="card-content">
      <p class="agent-description">{{ agent.description || '暂无描述' }}</p>

      <div v-if="agentTags.length > 0" class="agent-tags">
        <el-tag v-for="tag in agentTags" :key="tag._id" size="small" type="info" class="tag-item">
          {{ tag.name }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Icon from '@/components/mdiIicon/index.vue'

import type { Agent } from '../../types'

// 标签类型定义
interface Tag {
  _id: string
  name: string
}

interface Props {
  agent: Agent & { _id: string }
  allTags?: Tag[]
}

interface Emits {
  (e: 'edit', agent: Agent): void
  (e: 'delete', agent: Agent): void
  (e: 'execute', agent: Agent): void
}

const props = withDefaults(defineProps<Props>(), {
  allTags: () => []
})
const emit = defineEmits<Emits>()

// 计算当前智能体的标签
const agentTags = computed(() => {
  if (!props.agent.tags || !Array.isArray(props.agent.tags) || !props.allTags) {
    return []
  }
  //
  return props.agent.tags
    .map(tagId => props.allTags!.find(tag => tag._id === tagId))
    .filter(tag => tag !== undefined) as Tag[]
})

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '未知时间'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 处理编辑
const handleEdit = () => {
  emit('edit', props.agent)
}

// 处理执行
const handleExecute = () => {
  emit('execute', props.agent)
}

// 处理删除
const handleDelete = () => {
  emit('delete', props.agent)
}
</script>

<style scoped>
.agent-card {
  background: white;
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  padding: 24px;
  height: 180px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 20px;
}

.agent-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  border-color: var(--el-color-primary-light-7);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.agent-info {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.agent-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.3;
  letter-spacing: -0.01em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.agent-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  opacity: 0.8;
}

.meta-item .mdi-icon {
  font-size: 12px;
}

.card-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: all 0.3s ease;
  transform: translateY(-4px);
  flex-shrink: 0;
}

.agent-card:hover .card-actions {
  opacity: 1;
  transform: translateY(0);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.agent-description {
  /* margin: 0 0 12px 0; */
  color: var(--el-text-color-regular);
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  opacity: 0.85;
  text-align: left;
}

.agent-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag-item {
  font-size: 12px;
}

/* 按钮样式优化 */
.card-actions .el-button {
  transition: all 0.2s ease;
}

.card-actions .el-button:hover {
  transform: scale(1.1);
}
</style>
