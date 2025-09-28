<template>
  <div class="scheduler-page">
    <PageHeader title="定时任务管理" icon="clock">
      <template #actions>
        <el-button type="primary" @click="loadJobs">
          <Icon name="mdiRefresh" />
          刷新
        </el-button>
      </template>
    </PageHeader>

    <!-- 任务列表区域 -->
    <div class="task-list-section">

      <!-- 任务列表 -->
      <div class="task-list">
        <div v-if="jobs.length === 0" class="empty-state">
          <Icon name="mdiClipboardList" size="xlarge" class="empty-icon" />
          <h3>暂无定时任务</h3>
          <p>当前没有找到符合条件的定时任务</p>
        </div>

        <div v-else class="task-items">
          <TaskCard v-for="job in jobs" :key="job.jobName" :task="job" @task-cancelled="loadJobs" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import PageHeader from '@/components/pageHeader/index.vue'
import TaskCard from './components/TaskCard.vue'
import { schedulerAPI } from '../../api/scheduler'

// 响应式数据
const jobs = ref<any[]>([])

// 加载定时任务列表
const loadJobs = async () => {
  try {
    const response = await schedulerAPI.listJobs()
    jobs.value = response.data.list || []
  } catch (error) {
    console.error('加载定时任务失败:', error)
    ElMessage.error('加载定时任务失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadJobs()
})
</script>

<style scoped>
.scheduler-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-fill-color-extra-light);
  overflow: hidden;
  padding: 24px;
}

/* 任务列表样式 */
.task-list-section {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}


.task-list {
  background: transparent;
  border-radius: 12px;
  overflow: hidden;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--el-text-color-regular);
  background: white;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-lighter);
  margin: 16px;
}

.empty-icon {
  color: var(--el-text-color-placeholder);
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.task-items {
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

@media (max-width: 768px) {
  .scheduler-page {
    padding: 16px;
  }
}
</style>
