<template>
    <div class="time-display">
        <span class="label">{{ label }}：</span>
        <span class="value">{{ formattedTime }}</span>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import moment from 'moment'

interface Props {
    label: string
    time?: string | null
}

const props = defineProps<Props>()

const formattedTime = computed(() => {
    if (!props.time) return '-'
    try {
        return moment(props.time).format('YYYY-MM-DD HH:mm:ss')
    } catch {
        return props.time
    }
})
</script>

<style scoped>
.time-display {
    display: flex;
    align-items: flex-start;
    gap: 8px;
}

.label {
    font-weight: 500;
    color: var(--el-text-color-regular);
    min-width: 80px;
    flex-shrink: 0;
}

.value {
    color: var(--el-text-color-primary);
    word-break: break-all;
}
</style>
