<template>
    <div class="agent-card-item">
        <div class="agent-avatar">
            <Icon name="mdiAccount" size="medium" />
        </div>
        <div class="agent-info">
            <h5 class="agent-name">{{ agent.name }}</h5>
            <p class="agent-description">{{ agent.description || '暂无描述' }}</p>
        </div>
        <div class="agent-action">
            <Icon :name="actionIcon" @click="handleActionClick" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Icon from '@/components/mdiIicon/index.vue'
import type { Agent } from '../../types'

const props = defineProps<{
    agent: Agent
    actionType: 'add' | 'remove'
}>()

const emit = defineEmits<{
    action: [agent: Agent, actionType: 'add' | 'remove']
}>()

// 计算属性
const actionIcon = computed(() => {
    return props.actionType === 'add' ? 'mdiPlus' : 'mdiDelete'
})


// 操作按钮点击处理
const handleActionClick = (event: Event) => {
    event.stopPropagation() // 阻止事件冒泡到父元素
    emit('action', props.agent, props.actionType)
}
</script>

<style scoped>
.agent-card-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: white;
    border: 2px solid var(--el-border-color-light);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    height: 80px;
}

.agent-card-item:hover {
    border-color: var(--el-color-primary);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

.agent-avatar {
    margin-right: 12px;
}

.agent-info {
    flex: 1;
    min-width: 0;
}

.agent-name {
    margin: 0 0 4px 0;
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.agent-description {
    margin: 0;
    font-size: 12px;
    color: var(--el-text-color-regular);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.agent-action {
    margin-left: 12px;
    color: var(--el-color-primary);
    font-size: 18px;
}
</style>
