<template>
  <div class="model-card" @click="handleCardClick">
    <div class="card-header">
      <div class="model-info">
        <h3 class="model-name">{{ model.name }}</h3>
        <div class="model-meta">
          <span class="meta-item">
            <Icon name="mdiClock" size="small" />
            {{ formatDate(model._insertTime || model._updateTime) }}
          </span>
        </div>
      </div>
      <div class="card-actions">
        <el-button type="primary" size="small" circle @click.stop="handleEdit">
          <Icon name="mdiPencil" />
        </el-button>
        <el-button type="danger" size="small" circle @click.stop="handleDelete">
          <Icon name="mdiDelete" />
        </el-button>
      </div>
    </div>

    <div class="card-content">
      <p class="model-description">{{ model.description || '暂无描述' }}</p>

      <div class="model-config">
        <div class="config-item" v-if="model.modelName">
          <span class="config-label">模型:</span>
          <span class="config-value">{{ model.modelName }}</span>
        </div>
        <div class="config-item" v-if="model.maxTokens">
          <span class="config-label">最大Token:</span>
          <span class="config-value">{{ formatNumber(model.maxTokens) }}</span>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Icon from '../../../components/mdiIicon/index.vue'

import type { Model } from '../types'

interface Props {
  model: Model & { _id: string }
}

interface Emits {
  (e: 'edit', model: Model): void
  (e: 'delete', model: Model): void
  (e: 'click', model: Model): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

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

// 格式化数字（以1024为单位）
const formatNumber = (num: number) => {
  if (num >= 1024) {
    return (num / 1024).toFixed(0) + 'K'
  }
  return num.toString()
}

// 处理卡片点击
const handleCardClick = () => {
  emit('click', props.model)
}

// 处理编辑
const handleEdit = () => {
  emit('edit', props.model)
}

// 处理删除
const handleDelete = () => {
  emit('delete', props.model)
}
</script>

<style scoped>
.model-card {
  background: white;
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  padding: 24px;
  height: 240px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.model-card:hover {
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

.model-info {
  flex: 1;
}

.model-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.3;
  letter-spacing: -0.01em;
}

.model-meta {
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

.model-card:hover .card-actions {
  opacity: 1;
  transform: translateY(0);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.model-description {
  margin: 0 0 16px 0;
  color: var(--el-text-color-regular);
  line-height: 1.2;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  opacity: 0.85;
}

.model-config {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
  font-size: 12px;
}

.config-label {
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.config-value {
  color: var(--el-text-color-primary);
  font-weight: 600;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

/* 按钮样式优化 */
.card-actions .el-button {
  transition: all 0.2s ease;
}

.card-actions .el-button:hover {
  transform: scale(1.1);
}
</style>
