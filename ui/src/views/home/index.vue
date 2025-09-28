<template>
  <div class="welcome-page">
    <!-- 主标题区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="main-title">欢迎使用Orche智能体平台</h1>
        <p class="subtitle">构建、管理和执行智能体的统一平台</p>

        <!-- 功能卡片 -->
        <div class="feature-cards">
          <div class="feature-card" @click="navigateTo('/agent')">
            <div class="card-icon">
              <Icon name="mdiRobot" />
            </div>
            <h3>智能体管理</h3>
            <p>创建、编辑和管理您的智能体</p>
          </div>

          <div class="feature-card" @click="navigateTo('/modelSetting')">
            <div class="card-icon">
              <Icon name="mdiCog" />
            </div>
            <h3>模型设置</h3>
            <p>配置和管理AI模型参数</p>
          </div>

          <div class="feature-card" @click="navigateTo('/task')">
            <div class="card-icon">
              <Icon name="mdiClipboardList" />
            </div>
            <h3>任务管理</h3>
            <p>查看和管理执行任务</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速开始区域 -->
    <div class="quick-start-section">
      <div class="container">
        <h2>快速开始</h2>
        <div class="quick-actions">
          <el-button type="primary" size="large" @click="navigateTo('/modelSetting')">
            <Icon name="mdiCog" />
            配置大模型
          </el-button>
          <el-button type="default" size="large" @click="navigateTo('/agent')">
            <Icon name="mdiRobot" />
            创建智能体
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ stats.agentCount }}</div>
            <div class="stat-label">智能体数量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.modelCount }}</div>
            <div class="stat-label">模型数量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.taskCount }}</div>
            <div class="stat-label">执行次数</div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Icon from '@/components/mdiIicon/index.vue'
import request from '@/utils/request'

const router = useRouter()

// 统计数据
const stats = ref({
  agentCount: 0,
  taskCount: 0,
  modelCount: 0
})

// 导航函数
const navigateTo = (path: string) => {
  router.push(path)
}

// 加载统计数据
const loadStats = async () => {

  const response = await request.get('/home/summary')
  stats.value = response.data

}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.welcome-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.hero-section {
  padding: 80px 20px;
  text-align: center;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
}

.main-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.subtitle {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  opacity: 0.9;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.feature-card p {
  opacity: 0.8;
  line-height: 1.6;
}

.quick-start-section {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 4rem 20px;
  margin: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.quick-start-section h2 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  font-weight: 600;
}

.quick-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  padding: 1rem 2rem;
  font-size: 1.1rem;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stats-section {
  padding: 4rem 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.stat-item {
  text-align: center;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-number {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #ffd700;
}

.stat-label {
  font-size: 1.1rem;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.2rem;
  }

  .feature-cards {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    flex-direction: column;
    align-items: center;
  }

  .quick-actions .el-button {
    width: 100%;
    max-width: 300px;
  }
}
</style>