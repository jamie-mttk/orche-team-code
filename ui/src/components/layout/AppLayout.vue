<template>
  <div class="app-layout">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <!-- Logo区域 -->
      <div class="logo-section">
        <LogoIcon :size="32" />
      </div>

      <!-- 添加按钮 - 暂时隐藏,以后实现 -->
      <!-- <div class="add-section">
        <el-button type="primary" circle size="small" @click="handleAdd" class="add-btn">
          <Icon name="mdiPlus" />
        </el-button>
      </div> -->

      <!-- 功能导航 -->
      <div class="nav-section">
        <div v-for="item in navItems" :key="item.path" class="nav-item" :class="{ active: currentPath === item.path }"
          @click="navigateTo(item.path)">
          <Icon :name="item.icon" size="medium" class="nav-icon" />
          <span class="nav-text">{{ item.name }}</span>
        </div>
      </div>

      <!-- 底部用户区域 -->
      <div class="bottom-section">
        <!-- 文档/消息按钮 -->
        <div class="doc-msg-section">
          <!-- <div class="doc-msg-item" @click="handleFeature">
            <Icon name="message" size="medium" class="doc-msg-icon" />
            <span class="doc-msg-text">消息</span>
          </div> -->
        </div>

        <div class="user-menu">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <Icon name="mdiAccount" size="medium" class="user-icon" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="deploy">部署中心</el-dropdown-item>
                <el-dropdown-item command="setting">全局设置</el-dropdown-item>
                <!-- <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item> -->
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 右侧内容区域 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import LogoIcon from './LogoIcon.vue'
import Icon from '../mdiIicon/index.vue'

const route = useRoute()
const router = useRouter()

const currentPath = computed(() => route.path)

// 导航项配置
const navItems = [
  { name: '主页', path: '/', icon: 'mdiHome' },
  { name: '智能体', path: '/agent', icon: 'mdiRobot' },
  { name: '任务', path: '/task', icon: 'mdiClipboardList' },
  { name: '大模型', path: '/modelSetting', icon: 'mdiCog' },
  { name: '定时', path: '/scheduler', icon: 'mdiClock' }
]

// 导航到指定路径
const navigateTo = (path: string) => {
  router.push(path)
}



// 处理用户菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'deploy':
      router.push('/deploy')
      break
    case 'setting':
      router.push('/systemConfig')
      break
    case 'logout':
      ElMessage.success('退出登录成功')
      break
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

/* 左侧边栏样式 */
.sidebar {
  width: 60px;
  background: linear-gradient(180deg, var(--el-bg-color) 0%, var(--el-fill-color-extra-light) 100%);
  border-right: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
  position: relative;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, transparent 0%, var(--el-border-color-lighter) 20%, var(--el-border-color-lighter) 80%, transparent 100%);
}

.logo-section {
  padding: 16px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, transparent 100%);
  margin: 8px;
  border-radius: 8px;
}

.add-section {
  padding: 12px 0;
  display: flex;
  justify-content: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin: 0 8px;
}

.add-btn {
  width: 32px;
  height: 32px;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.nav-section {
  flex: 1;
  padding: 16px 0;
  margin: 0 8px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 4px 0;
  border-radius: 8px;
  position: relative;
}

.nav-item:hover {
  background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color-extra-light) 100%);
  color: var(--el-color-primary);
  transform: translateX(2px);
}

.nav-item.active {
  background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-8) 100%);
  color: var(--el-color-primary);
  border: 1px solid var(--el-color-primary);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.nav-icon {
  color: inherit;
}

.nav-text {
  font-size: 10px;
  font-weight: 500;
  text-align: center;
  line-height: 1;
  letter-spacing: 0.5px;
}

.bottom-section {
  padding: 16px 0;
  border-top: 1px solid var(--el-border-color-lighter);
  margin: 0 8px;
  background: linear-gradient(180deg, transparent 0%, var(--el-fill-color-extra-light) 100%);
  border-radius: 8px 8px 0 0;
}

.doc-msg-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
  padding: 0 8px;
}

.doc-msg-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 10px 0;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 6px;
  position: relative;
}

.doc-msg-item:hover {
  background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color-extra-light) 100%);
  color: var(--el-color-primary);
  transform: translateX(2px);
}

.doc-msg-icon {
  color: inherit;
}

.doc-msg-text {
  font-size: 9px;
  text-align: center;
  line-height: 1;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.user-menu {
  padding: 0 8px;
}

.user-info {
  display: flex;
  justify-content: center;
  cursor: pointer;
  padding: 10px 0;
  border-radius: 6px;
  transition: all 0.2s;
}

.user-info:hover {
  background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color-extra-light) 100%);
  transform: translateX(2px);
}

.user-icon {
  color: var(--el-text-color-primary);
}

/* 右侧内容区域样式 */
.main-content {
  flex: 1;
  background-color: var(--el-fill-color-extra-light);
  overflow-y: auto;
  padding: 0;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 60px;
  }

  .nav-text {
    font-size: 9px;
  }

  .doc-msg-text {
    font-size: 8px;
  }
}
</style>
