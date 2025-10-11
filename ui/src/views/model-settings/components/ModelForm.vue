<template>
  <div class="form-container">
    <div class="form-header">
      <h2>{{ isEdit ? '修改模型' : '新增模型' }}</h2>
      <el-button @click="$emit('back')">返回列表</el-button>
    </div>

    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px" class="model-form">
      <el-form-item label="模型名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入模型名称" :disabled="submitting" />
      </el-form-item>

      <el-form-item label="模型描述" prop="description">
        <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入模型描述"
          :disabled="submitting" />
      </el-form-item>

      <el-form-item label="API基础URL" prop="apiBaseUrl">
        <el-input v-model="formData.apiBaseUrl"
          placeholder="请输入API基础URL，如：https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
          :disabled="submitting" />
      </el-form-item>

      <el-form-item label="API密钥" prop="apiKey">
        <el-input v-model="formData.apiKey" type="password" placeholder="请输入API密钥" :disabled="submitting"
          show-password />
      </el-form-item>

      <el-form-item label="模型名称" prop="modelName">
        <el-input v-model="formData.modelName" placeholder="请输入模型名称，如：qwen-plus-2025-07-28" :disabled="submitting" />
      </el-form-item>

      <el-form-item label="最大Token数" prop="maxTokens">
        <el-input-number v-model="maxTokensInK" :min="1" :max="976" :step="1" placeholder="请输入最大Token数（K）"
          :disabled="submitting" style="width: 100%" />
        <div class="slider-description">
          <span class="description-text">以K为单位（1K = 1024 Token），实际值：{{ (formData.maxTokens ?? 0).toLocaleString()
          }}</span>
        </div>
      </el-form-item>

      <el-form-item label="温度参数" prop="temperature">
        <el-slider v-model="formData.temperature" :min="0" :max="2" :step="0.1" :disabled="submitting" show-input
          input-size="large" :format-tooltip="(val: number) => val.toFixed(1)" />
        <div class="slider-description">
          <span class="description-text">0.0: 确定性输出，2.0: 创造性输出</span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
          {{ isEdit ? '保存修改' : '新增模型' }}
        </el-button>
        <el-button @click="$emit('back')" size="large" :disabled="submitting">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { FormInstance } from 'element-plus'

import type { Model } from '../types'

interface Props {
  model?: Model
  isEdit: boolean
  submitting: boolean
}

interface Emits {
  (e: 'submit', data: Model): void
  (e: 'back'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<Model>({
  name: '',
  description: '',
  apiBaseUrl: '',
  apiKey: '',
  modelName: '',
  maxTokens: 65536,
  temperature: 0.0,
  _id: undefined
})

// 以K为单位的最大Token数（计算属性）
const maxTokensInK = computed({
  get: () => {
    return Math.round((formData.value.maxTokens ?? 65536) / 1024)
  },
  set: (value: number) => {
    formData.value.maxTokens = value * 1024
  }
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入模型名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  apiBaseUrl: [
    { required: true, message: '请输入API基础URL', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入API密钥', trigger: 'blur' }
  ],
  modelName: [
    { required: true, message: '请输入模型名称', trigger: 'blur' }
  ],
  maxTokens: [
    { required: true, message: '请输入最大Token数', trigger: 'blur' },
    { type: 'number', min: 1, message: 'Token数必须大于0', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '请设置温度参数', trigger: 'blur' },
    { type: 'number', min: 0, max: 2, message: '温度参数必须在0-2之间', trigger: 'blur' }
  ]
}

// 监听model变化，更新表单数据
watch(() => props.model, (newModel) => {
  if (newModel) {
    formData.value = { ...newModel }
  } else {
    formData.value = {
      name: '',
      description: '',
      apiBaseUrl: '',
      apiKey: '',
      modelName: '',
      maxTokens: 65536,
      temperature: 0.0,
      _id: undefined
    }
  }
}, { immediate: true })

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    emit('submit', formData.value)
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped>
.form-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 40px;
  background: white;
  border-radius: 0;
  box-shadow: none;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 2px solid var(--el-border-color-lighter);
}

.form-header h2 {
  margin: 0;
  color: var(--el-text-color-primary);
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.model-form {
  flex: 1;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.model-form .el-form-item {
  margin-bottom: 36px;
}

.model-form .el-form-item__label {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.model-form .el-input,
.model-form .el-select,
.model-form .el-input-number {
  font-size: 16px;
}

.model-form .el-input__wrapper,
.model-form .el-select .el-input__wrapper,
.model-form .el-input-number__wrapper {
  border-radius: 10px;
  padding: 14px 18px;
  border: 2px solid var(--el-border-color-light);
  transition: all 0.3s ease;
}

.model-form .el-input__wrapper:hover,
.model-form .el-select .el-input__wrapper:hover,
.model-form .el-input-number__wrapper:hover {
  border-color: var(--el-color-primary-light-7);
}

.model-form .el-input__wrapper.is-focus,
.model-form .el-select .el-input__wrapper.is-focus,
.model-form .el-input-number__wrapper.is-focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.model-form .el-textarea__inner {
  font-size: 16px;
  line-height: 1.6;
  border-radius: 10px;
  padding: 18px;
  border: 2px solid var(--el-border-color-light);
  transition: all 0.3s ease;
  resize: vertical;
  min-height: 120px;
}

.model-form .el-textarea__inner:hover {
  border-color: var(--el-color-primary-light-7);
}

.model-form .el-textarea__inner:focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.model-form .el-button {
  padding: 16px 36px;
  font-size: 16px;
  margin-right: 24px;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.model-form .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.model-form .el-button--primary {
  background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
  border: none;
}

.model-form .el-button--primary:hover {
  background: linear-gradient(135deg, var(--el-color-primary-dark-2) 0%, var(--el-color-primary) 100%);
}

/* 滑块样式优化 */
.model-form .el-slider {
  margin-top: 8px;
}

.slider-description {
  margin-top: 8px;
  text-align: center;
}

.description-text {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  opacity: 0.8;
}

/* 表单验证样式 */
.model-form .el-form-item.is-error .el-input__wrapper,
.model-form .el-form-item.is-error .el-textarea__inner,
.model-form .el-form-item.is-error .el-input-number__wrapper {
  border-color: var(--el-color-danger);
}

.model-form .el-form-item.is-error .el-input__wrapper:focus,
.model-form .el-form-item.is-error .el-textarea__inner:focus,
.model-form .el-form-item.is-error .el-input-number__wrapper:focus {
  box-shadow: 0 0 0 2px var(--el-color-danger-light-8);
}
</style>
