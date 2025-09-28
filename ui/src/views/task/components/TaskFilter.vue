<template>
    <div class="filter-section">
        <el-form :model="filterForm" class="filter-form">

            <div class="form-content">
                <el-form-item>
                    <el-input v-model="filterForm.name" placeholder="搜索任务名称..." clearable>
                        <template #prefix>
                            <Icon name="magnify" size="small" />
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-select v-model="filterForm.status" placeholder="状态筛选" clearable style="width: 320px">
                        <el-option label="全部" value="" />
                        <el-option label="执行中" value="running" />
                        <el-option label="已完成" value="success" />
                        <el-option label="执行失败" value="failed" />
                        <el-option label="等待处理" value="waiting" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-date-picker v-model="filterForm.dateRange" type="datetimerange" range-separator="至"
                        start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss"
                        value-format="YYYY-MM-DD HH:mm:ss" :shortcuts="dateShortcuts" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleQuery">
                        <Icon name="magnify" size="small" />
                        查询
                    </el-button>
                    <el-button @click="handleClearFilters">
                        <Icon name="mdiFilter" size="small" />
                        重置条件
                    </el-button>
                </el-form-item>
            </div>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import moment from 'moment'
import Icon from '@/components/mdiIicon/index.vue'

//
const filterForm: any = defineModel()

// 日期快捷选择配置
const dateShortcuts = [
    {
        text: '今天',
        value: () => {
            const today = moment().format('YYYY-MM-DD 00:00:00')
            const nextDay = moment().add(1, 'day').format('YYYY-MM-DD 00:00:00')
            return [today, nextDay]
        }
    },
    {
        text: '昨天',
        value: () => {
            const yesterday = moment().subtract(1, 'day').format('YYYY-MM-DD 00:00:00')
            const today = moment().format('YYYY-MM-DD 00:00:00')
            return [yesterday, today]
        }
    },
    {
        text: '前天',
        value: () => {
            const dayBeforeYesterday = moment().subtract(2, 'day').format('YYYY-MM-DD 00:00:00')
            const yesterday = moment().subtract(1, 'day').format('YYYY-MM-DD 00:00:00')
            return [dayBeforeYesterday, yesterday]
        }
    },
    {
        text: '本周',
        value: () => {
            const startOfWeek = moment().startOf('week').format('YYYY-MM-DD 00:00:00')
            const nextWeek = moment().add(1, 'week').startOf('week').format('YYYY-MM-DD 00:00:00')
            return [startOfWeek, nextWeek]
        }
    },
    {
        text: '本月',
        value: () => {
            const startOfMonth = moment().startOf('month').format('YYYY-MM-DD 00:00:00')
            const nextMonth = moment().add(1, 'month').startOf('month').format('YYYY-MM-DD 00:00:00')
            return [startOfMonth, nextMonth]
        }
    }
]

// Emits
const emit = defineEmits<{
    query: []
}>()

// 方法
const handleQuery = () => {
    emit('query')
}

const handleClearFilters = () => {
    filterForm.value.name = ''
    filterForm.value.status = ''

    // 使用 moment 设置日期范围为当前日期到第二天
    const today = moment().format('YYYY-MM-DD 00:00:00')
    const tomorrow = moment().add(1, 'day').format('YYYY-MM-DD 00:00:00')

    filterForm.value.dateRange = [today, tomorrow]
}

onMounted(() => handleClearFilters())
</script>

<style scoped>
.filter-section {
    padding: 20px 24px;
    background: white;
    border-bottom: 1px solid var(--el-border-color-lighter);
}

.filter-form {
    width: 100%;
}

.form-content {
    display: flex;
    gap: 16px;
    align-items: flex-end;
    flex-wrap: wrap;
}

.form-content .el-form-item {
    margin-bottom: 0;
}

.form-content .el-form-item:first-child {
    flex: 1;
    min-width: 200px;
    max-width: 400px;
}

.form-content .el-form-item:last-child {
    display: flex;
    gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .form-content {
        flex-direction: column;
        align-items: stretch;
    }

    .form-content .el-form-item:first-child {
        max-width: none;
    }

    .form-content .el-form-item:last-child {
        justify-content: flex-end;
    }
}
</style>
