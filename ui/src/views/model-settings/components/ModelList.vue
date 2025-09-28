<template>
  <div class="model-list-view">
    <!-- 页面头部 -->
    <PageHeader title="模型列表" icon="cog" :show-add-button="true" add-button-text="新增模型" @add="$emit('add')">
      <template #content>
        <el-input :model-value="searchName" @update:model-value="$emit('update:searchName', $event)"
          placeholder="按名称搜索模型" style="width: 300px;" clearable>
          <template #prefix>
            <Icon name="magnify" />
          </template>
        </el-input>
      </template>
    </PageHeader>

    <!-- 模型卡片列表 -->
    <div class="model-cards">
      <ModelCard v-for="model in filteredModels" :key="model._id || ''" :model="model as Model & { _id: string }"
        @edit="$emit('edit', $event)" @delete="$emit('delete', $event)" @click="$emit('view', $event)" />
    </div>

    <!-- 空状态 -->
    <el-empty v-if="filteredModels.length === 0" description="暂无模型数据" class="empty-state" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ModelCard from './ModelCard.vue'
import Icon from '../../../components/mdiIicon/index.vue'
import PageHeader from '../../../components/pageHeader/index.vue'

import type { Model } from '../types'

interface Props {
  models: Model[]
  searchName: string
}

interface Emits {
  (e: 'add'): void
  (e: 'edit', model: Model): void
  (e: 'delete', model: Model): void
  (e: 'view', model: Model): void
  (e: 'update:searchName', value: string): void
}

const props = defineProps<Props>()
defineEmits<Emits>()

// 过滤后的模型列表
const filteredModels = computed(() => {
  if (!props.searchName) return props.models
  return props.models.filter(model =>
    model.name.toLowerCase().includes(props.searchName.toLowerCase())
  )
})
</script>

<style scoped>
.model-list-view {
  flex: 1;
  display: flex;
  flex-direction: column;
}


.model-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
  flex: 1;
  overflow-y: auto;
}

.empty-state {
  margin: 40px 0;
}
</style>
