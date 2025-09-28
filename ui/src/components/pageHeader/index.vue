<template>
    <div class="page-header">
        <div class="header-content">
            <div class="header-left">
                <Icon :name="icon" size="large" class="title-icon" />
                <h1 class="page-title">{{ title }}</h1>
            </div>
            <div class="header-right">
                <slot name="actions">
                    <el-button v-if="showAddButton" type="primary" @click="$emit('add')">
                        <Icon name="mdiPlus" />
                        {{ addButtonText }}
                    </el-button>
                </slot>
            </div>
        </div>
        <div v-if="$slots.content" class="header-content-slot">
            <slot name="content" />
        </div>
    </div>
</template>

<script setup lang="ts">
import Icon from '../mdiIicon/index.vue'

interface Props {
    title: string
    icon: string
    showAddButton?: boolean
    addButtonText?: string
}

withDefaults(defineProps<Props>(), {
    showAddButton: false,
    addButtonText: '新增'
})

defineEmits<{
    add: []
}>()
</script>

<style scoped>
.page-header {
    margin-bottom: 10px;
    flex-shrink: 0;
    padding: 20px;
    background: var(--el-bg-color);
    border-radius: var(--el-border-radius-base);
    border: 1px solid var(--el-border-color-light);
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.title-icon {
    color: var(--el-color-primary);
}

.page-title {
    font-size: 1.5rem;
    font-weight: 600;
    margin: 0;
    color: var(--el-text-color-primary);
}

.header-right {
    display: flex;
    align-items: center;
    gap: 12px;
}

.header-content-slot {
    margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .header-content {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }

    .header-right {
        width: 100%;
        justify-content: flex-end;
    }
}
</style>
