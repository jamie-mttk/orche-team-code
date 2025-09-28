<template>
    <div class="stats-section">
        <div class="stats-header">
            <div class="date-selector">
                <div class="current-date-range">
                    <span class="date-range-label">当前范围：</span>
                    <span class="date-range-text">{{ currentDateRangeText }}</span>
                </div>
                <el-select v-model="selectedDateRange" @change="handleDateRangeChange" size="small"
                    style="width: 120px;">
                    <el-option label="今天" value="today" />
                    <el-option label="昨天" value="yesterday" />
                    <el-option label="本周" value="week" />
                    <el-option label="本月" value="month" />
                    <el-option label="全部" value="all" />
                    <el-option label="定制" value="custom" />
                </el-select>
            </div>
        </div>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon running">
                    <Icon name="mdiPlayCircle" size="large" />
                </div>
                <div class="stat-content">
                    <div class="stat-number">{{ taskStats.running || 0 }}</div>
                    <div class="stat-label">执行中</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon success">
                    <Icon name="mdiCheckCircle" size="large" />
                </div>
                <div class="stat-content">
                    <div class="stat-number">{{ taskStats.success || 0 }}</div>
                    <div class="stat-label">已完成</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon failed">
                    <Icon name="mdiCloseCircle" size="large" />
                </div>
                <div class="stat-content">
                    <div class="stat-number">{{ taskStats.failed || 0 }}</div>
                    <div class="stat-label">执行失败</div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon waiting">
                    <Icon name="mdiClock" size="large" />
                </div>
                <div class="stat-content">
                    <div class="stat-number">{{ totalCount }}</div>
                    <div class="stat-label">全部</div>
                </div>
            </div>
        </div>
    </div>

    <!-- 自定义日期范围弹窗 -->
    <el-dialog v-model="customDateDialogVisible" title="选择日期范围" width="640px" :before-close="handleCustomDateClose">
        <div class="custom-date-dialog">
            <div class="date-picker-row">
                <label class="date-label">时间范围：</label>
                <el-date-picker v-model="customDateRange" type="datetimerange" range-separator="至"
                    start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY/MM/DD HH:mm:ss"
                    value-format="YYYY/MM/DD HH:mm:ss" style="width: 100%;" />
            </div>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleCustomDateClose">取消</el-button>
                <el-button type="primary" @click="handleCustomDateConfirm"
                    :disabled="!customDateRange || customDateRange.length !== 2">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import Icon from '@/components/mdiIicon/index.vue'
import moment from 'moment'
import request from '@/utils/request'

// 统计数据 - 直接保存API返回结果
const taskStats = ref({})

// 计算全部数量
const totalCount = computed(() => {
    return taskStats.value.running || 0 + taskStats.value.success || 0 + taskStats.value.failed || 0
})







// 日期选择相关
const selectedDateRange = ref('today')
const customDateRange = ref<[string, string] | null>(null)
const customDateDialogVisible = ref(false)

// 当前选择的日期范围文本
const currentDateRangeText = computed(() => {
    if (selectedDateRange.value === 'custom') {
        if (customDateRange.value && customDateRange.value.length === 2) {
            const [startTime, endTime] = customDateRange.value
            return `${startTime} 至 ${endTime}`
        }
        return '请选择时间范围'
    }

    const range = dateRangeOptions[selectedDateRange.value as keyof typeof dateRangeOptions]()
    if (range.start && range.end) {
        return `${range.start} 至 ${range.end}`
    }

    return '全部时间'
})

// 定义日期范围选项
const dateRangeOptions = {
    today: () => {
        const start = moment().startOf('day').format('YYYY/MM/DD HH:mm:ss')
        const end = moment().add(1, 'day').startOf('day').format('YYYY/MM/DD HH:mm:ss')
        return { start, end }
    },
    yesterday: () => {
        const start = moment().subtract(1, 'day').startOf('day').format('YYYY/MM/DD HH:mm:ss')
        const end = moment().startOf('day').format('YYYY/MM/DD HH:mm:ss')
        return { start, end }
    },
    week: () => {
        const start = moment().startOf('week').format('YYYY/MM/DD HH:mm:ss')
        const end = moment().add(1, 'week').startOf('week').format('YYYY/MM/DD HH:mm:ss')
        return { start, end }
    },
    month: () => {
        const start = moment().startOf('month').format('YYYY/MM/DD HH:mm:ss')
        const end = moment().add(1, 'month').startOf('month').format('YYYY/MM/DD HH:mm:ss')
        return { start, end }
    },
    all: () => {
        return { start: undefined, end: undefined }
    }
}

// 处理日期范围变化
const handleDateRangeChange = (value: string) => {
    console.log('Date range changed to:', value)
    if (value === 'custom') {
        customDateDialogVisible.value = true
        return
    }

    try {
        const range = dateRangeOptions[value as keyof typeof dateRangeOptions]()
        loadTaskStat(range.start, range.end)
    } catch (error) {
        console.error('Error in handleDateRangeChange:', error)
    }
}

// 处理自定义日期弹窗关闭
const handleCustomDateClose = () => {
    customDateDialogVisible.value = false
    // 重置选择器为之前的值
    selectedDateRange.value = 'today'
    customDateRange.value = null
}

// 处理自定义日期确认
const handleCustomDateConfirm = () => {
    if (customDateRange.value && customDateRange.value.length === 2) {
        const [startTime, endTime] = customDateRange.value
        loadTaskStat(startTime, endTime)
        customDateDialogVisible.value = false
        // 保持选择器为"定制"状态
    }
}

async function loadTaskStat(startTime?: string, endTime?: string) {
    // console.log('Loading task stats:', { startTime, endTime })

    // 构建请求参数
    const params: any = {}
    if (startTime) {
        params.startTime = startTime
    }
    if (endTime) {
        params.endTime = endTime
    }

    // 调用API
    const response = await request.get('/task/summarize', { params })
    taskStats.value = response.data

    // console.log('Task stats loaded:', taskStats.value)
}

// 初始化时加载今天的统计数据
onMounted(() => {
    const todayRange = dateRangeOptions.today()
    loadTaskStat(todayRange.start, todayRange.end)
})

</script>

<style scoped>
.stats-section {
    padding: 24px;
    background: white;
    border-bottom: 1px solid var(--el-border-color-lighter);
}

.stats-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
}

.date-selector {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
}

.current-date-range {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    background: var(--el-fill-color-lighter);
    border-radius: 4px;
    border: 1px solid var(--el-border-color-light);
}

.date-range-label {
    color: var(--el-text-color-secondary);
    font-size: 12px;
}

.date-range-text {
    color: var(--el-text-color-primary);
    font-size: 12px;
    font-weight: 500;
}

.custom-date-dialog {
    padding: 20px 0;
}

.date-picker-row {
    display: flex;
    align-items: center;
}

.date-label {
    width: 100px;
    font-size: 14px;
    color: var(--el-text-color-primary);
    margin-right: 12px;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
}

.stat-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: white;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.stat-icon.running {
    background: linear-gradient(135deg, #ff9500 0%, #ffb84d 100%);
    color: white;
}

.stat-icon.success {
    background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
    color: white;
}

.stat-icon.failed {
    background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
    color: white;
}

.stat-icon.waiting {
    background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
    color: white;
}

.stat-content {
    flex: 1;
}

.stat-number {
    font-size: 28px;
    font-weight: 700;
    color: var(--el-text-color-primary);
    line-height: 1;
}

.stat-label {
    font-size: 14px;
    color: var(--el-text-color-regular);
    margin-top: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}
</style>
