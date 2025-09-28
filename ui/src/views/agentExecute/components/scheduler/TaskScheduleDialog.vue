<template>
    <el-dialog title="任务定时设置" v-model="visible" width="30%" :append-to-body="true" :close-on-click-modal="false"
        :close-on-press-escape="false">
        <CdForm ref="taskScheduleFormRef" :data="data" :config="scheduleConfig">
        </CdForm>

        <template v-slot:footer>
            <span class="dialog-footer">
                <el-button @click="visible = false">取 消</el-button>
                <el-button type="primary" @click="handleSave">确 定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import CdForm from '@/components/ConfigDyna/Form.vue'
import { scheduleConfig } from './data'
defineOptions({
    inheritAttrs: false
})
// const visible = defineModel('visible', { default: false })
const visible = defineModel()
const emit = defineEmits(['saved'])
//
const taskScheduleFormRef = ref()
const data = ref({})

//
const handleSave = async () => {
    try {
        // 验证基本信息表单
        await taskScheduleFormRef.value?.validate()
        visible.value = false
        //
        emit('saved', data.value)
    } catch (error) {
        // 验证失败，显示错误信息
        ElMessage.error('请检查表单填写是否正确')
    }
}
</script>
<style lang="scss"></style>
