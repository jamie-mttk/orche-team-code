<template>
    <div class="members-section">
        <!-- 已选择的合作智能体 -->
        <div class="selected-members" v-if="selectedMembers.length > 0">
            <el-row :gutter="16" class="selected-grid">
                <el-col v-for="member in selectedMembers" :key="member._id" :span="4" :xs="12" :sm="8" :md="6" :lg="4"
                    :xl="4">
                    <AgentCardItem :agent="member" action-type="remove" @action="removeMember" />
                </el-col>
            </el-row>
        </div>


        <!-- 智能体选择器对话框 -->
        <AgentMemberSelector v-model="showMemberSelector" :selected-members="modelValue" :exclude-id="excludeId"
            @add="handleAddMember" />
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

import AgentCardItem from '../list/AgentCardItem.vue'
import AgentMemberSelector from './AgentMemberSelector.vue'
import { agentAPI } from '@/api'
import type { Agent } from '../../types'

const props = defineProps<{
    modelValue: string[]
    excludeId?: string
    showDialog?: boolean
}>()

const emit = defineEmits<{
    'update:modelValue': [value: string[]]
    'update:showDialog': [value: boolean]
}>()

// 智能体选择器状态
const showMemberSelector = computed({
    get: () => props.showDialog || false,
    set: (value: boolean) => emit('update:showDialog', value)
})

// 智能体列表
const allAgents = ref<Agent[]>([])

// 计算属性 - 从智能体列表中获取已选择的成员信息
const selectedMembers = computed(() => {
    if (!props.modelValue || !allAgents.value.length) return []
    return allAgents.value.filter(agent =>
        props.modelValue.includes(agent._id || '')
    )
})

// 加载智能体列表
const loadAgents = async () => {
    try {
        const response = await agentAPI.searchAgents()
        allAgents.value = response.data.list || []
    } catch (error) {
        console.error('加载智能体列表失败:', error)
        ElMessage.error('加载智能体列表失败')
        allAgents.value = []
    }
}

// 添加成员处理
const handleAddMember = (agentId: string) => {
    const newMembers = [...props.modelValue]
    if (!newMembers.includes(agentId)) {
        newMembers.push(agentId)
        emit('update:modelValue', newMembers)
    }
}

// 移除成员
const removeMember = (agent: Agent) => {
    if (!agent._id) return
    const newMembers = props.modelValue.filter(id => id !== agent._id)
    emit('update:modelValue', newMembers)
}

// 生命周期
onMounted(() => {
    loadAgents()
})
</script>

<style scoped>
.members-section {
    padding: 20px 0;
}

.selected-members {
    margin-bottom: 20px;
    padding: 20px;
    background: var(--el-color-success-light-9);
    border-radius: 8px;
    border: 1px solid var(--el-color-success-light-7);
}

.selected-grid {
    margin-bottom: 20px;
}

.add-members-section {
    text-align: center;
    padding: 20px;
}
</style>
