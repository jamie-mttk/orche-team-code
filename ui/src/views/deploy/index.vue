<template>
  <div class="deploy-page">
    <!-- 页面头部 -->
    <PageHeader title="部署管理" icon="upload" />

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 上传区域 -->
      <el-card class="upload-section">
        <template #header>
          <div class="section-header">
            <Icon name="mdiUpload" />
            <span>文件部署</span>
          </div>
        </template>

        <div class="upload-content">
          <div class="upload-tip">
            <Icon name="mdiInformation" />
            <span>请选择要部署的文件</span>
          </div>

          <el-upload ref="uploadRef" action="/deploy/doDeploy" :http-request="uploadFile" :multiple="false"
            :show-file-list="false" :limit="1" :on-exceed="handleExceed" class="upload-component">
            <el-button type="primary" size="large">
              <Icon name="mdiUpload" />
              点击上传部署
            </el-button>
          </el-upload>
        </div>
      </el-card>

      <!-- 结果显示区域 -->
      <el-card class="result-section">
        <template #header>
          <div class="section-header">
            <Icon name="mdiFileDocument" />
            <span>部署结果</span>
          </div>
        </template>

        <div class="result-content">
          <el-input type="textarea" :rows="20" placeholder="部署结果将在此处显示..." v-model="deployResult" readonly
            class="result-textarea" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import PageHeader from '@/components/pageHeader/index.vue'
import Icon from '@/components/mdiIicon/index.vue'

// 响应式数据
const deployResult = ref('')
const uploadRef = ref()

// 方法
const uploadFile = (params) => {
  const param = new FormData()
  param.append('file', params.file)

  request({
    method: 'post',
    url: params.action,
    headers: { 'Content-Type': 'multipart/form-data' },
    data: param
  })
    .then(function (response) {
      deployResult.value = parseResult(response.data)
    })
    .finally(function () {
      uploadRef.value.clearFiles()
    })
}

const handleExceed = () => {
  ElMessage.warning('上传文件数量超出限制')
}

const parseResult = (data) => {
  let msg = ''

  if (data.services && data.services.length > 0) {
    msg += '服务列表,共' + data.services.length + '条\n'
    for (let f of data.services) {
      msg +=
        '\t' +
        statusName(f) +
        '\t' +
        f.category +
        '\t' +
        f.key +
        '\t' +
        f.name +
        '\t' +
        '\t' +
        f.implClass +
        '\n'
    }
  } else {
    msg += '没有部署服务\n'
  }

  if (data.agentTemplates && data.agentTemplates.length > 0) {
    msg += '智能体模板列表,共' + data.agentTemplates.length + '条\n'
    for (let f of data.agentTemplates) {
      msg +=
        '\t' +
        statusName(f) +
        '\t' +
        f.key +
        '\t' +
        f.name +
        '\t' +
        f.description +
        '\t' +
        f.implClass +
        '\n'
    }
  } else {
    msg += '没有部署智能体模板\n'
  }

  if (data.plugins && data.plugins.length > 0) {
    msg += '插件列表,共' + data.plugins.length + '条\n'
    for (let f of data.plugins) {
      msg +=
        '\t' +
        statusName(f) +
        '\t' +
        f.key +
        '\t' +
        f.name +
        '\t' +
        f.serviceClass +
        '\t' +
        f.implClass +
        '\n'
    }
  } else {
    msg += '没有部署插件\n'
  }

  if (data.throwables && data.throwables.length > 0) {
    msg += '异常列表,共' + data.throwables.length + '条\n'
    for (let f of data.throwables) {
      msg += f + '\n'
    }
  } else {
    msg += '没有异常\n'
  }

  return msg
}

// 根据不同类型返回说明
const statusName = (line) => {
  const status = line.status
  if ('new' == status) {
    return '[新增]'
  } else if ('replace' == status) {
    return '[替换]'
  } else if ('ignore' == status) {
    return '[忽略]'
  } else if ('suppress' == status) {
    return '[禁用]'
  } else {
    return '[]'
  }
}

</script>

<style scoped>
.deploy-page {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.upload-section,
.result-section {
  border-radius: var(--el-border-radius-base);
  box-shadow: var(--el-box-shadow-light);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.upload-component {
  display: flex;
  justify-content: center;
}

.result-content {
  padding: 20px;
}

.result-textarea {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
}

.result-textarea :deep(.el-textarea__inner) {
  background-color: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-light);
  color: var(--el-text-color-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .deploy-page {
    padding: 10px;
  }

  .upload-content {
    padding: 15px;
  }

  .result-content {
    padding: 15px;
  }
}
</style>
