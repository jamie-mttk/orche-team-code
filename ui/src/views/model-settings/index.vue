<template>
  <div class="model-management-page">
    <!-- 模型列表界面 -->
    <ModelList v-if="!showForm" :models="models" :search-name="searchName" @add="showAddForm" @edit="showEditForm"
      @delete="handleDelete" @view="handleView" @update:search-name="searchName = $event" />

    <!-- 新增/修改界面 -->
    <ModelForm v-else :model="currentModel" :is-edit="isEdit" :submitting="submitting" @submit="handleSubmit"
      @back="backToList" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { modelAPI } from '../../api'
import ModelList from './components/ModelList.vue'
import ModelForm from './components/ModelForm.vue'

import type { Model } from './types'

// 界面状态
const showForm = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentModel = ref<Model | undefined>()

// 搜索
const searchName = ref('')

// 模型列表
const models = ref<Model[]>([])

// 加载模型列表
const loadModels = async () => {
  try {
    const response = await modelAPI.searchModels()
    models.value = response.data.list || []
  } catch (error) {
    console.error('加载模型列表失败:', error)
    ElMessage.error('加载模型列表失败')
    // 如果API失败，使用模拟数据作为备用
    models.value = [

    ]
  }
}

// 显示新增表单
const showAddForm = () => {
  isEdit.value = false
  currentModel.value = undefined
  showForm.value = true
}

// 显示修改表单
const showEditForm = (model: Model) => {
  isEdit.value = true
  currentModel.value = model
  showForm.value = true
}

// 返回列表
const backToList = () => {
  showForm.value = false
  isEdit.value = false
  currentModel.value = undefined
}

// 提交表单
const handleSubmit = async (formData: Model) => {
  try {
    submitting.value = true

    if (isEdit.value) {
      // 修改模型 - 需要传递_id字段
      const updateData = { ...formData, _id: currentModel.value?._id }
      await modelAPI.saveModel(updateData)
      ElMessage.success('修改成功')
    } else {
      // 新增模型
      await modelAPI.saveModel(formData)
      ElMessage.success('新增成功')
    }

    backToList()
    loadModels() // 刷新列表
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

// 删除模型
const handleDelete = async (model: Model) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模型 "${model.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (model._id) {
      await modelAPI.deleteModel(model._id)
    }
    ElMessage.success('删除成功')
    loadModels() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 查看模型详情
const handleView = (model: Model) => {
  ElMessage.info(`查看模型: ${model.name}`)
  // 这里可以添加查看详情的逻辑
}

// 页面加载时获取模型列表
onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.model-management-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: var(--el-fill-color-extra-light);
}
</style>
