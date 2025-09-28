<template>
  <div v-if="shouldShowBanner" class="init-status-banner warning">
    <div class="banner-content">
      <Icon name="mdiAlertCircle" size="small" class="status-icon" />
      <span class="status-text">系统尚未初始化，请进行初始化配置</span>
      <el-button type="primary" size="small" @click="handleInit">
        立即初始化
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from './mdiIicon/index.vue'
import { useInitCheck } from '@/utils/initCheck'

const { isInitialized, hasChecked } = useInitCheck()

// 仅在检测完成且initFlag为false时显示横幅
const shouldShowBanner = computed(() => {
  return hasChecked.value && isInitialized.value === false
})

const handleInit = () => {
  ElMessage.info('初始化功能开发中...')
  // TODO: 实现初始化流程
}
</script>

<style scoped>
.init-status-banner {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.init-status-banner.success {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-bottom: 1px solid #0ea5e9;
  color: #0c4a6e;
}

.init-status-banner.warning {
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border-bottom: 1px solid #f59e0b;
  color: #92400e;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 1200px;
  width: 100%;
  justify-content: center;
}

.status-icon {
  flex-shrink: 0;
}

.status-text {
  font-size: 14px;
  font-weight: 500;
  flex: 1;
  text-align: center;
}

/* 当显示横幅时，为页面内容添加顶部间距 */
:global(body) {
  transition: padding-top 0.3s ease;
}

:global(.init-banner-active) {
  padding-top: 60px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }

  .status-text {
    font-size: 13px;
  }
}
</style>
