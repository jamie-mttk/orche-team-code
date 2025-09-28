<template>
    <div class="task-management-page">
        <!-- 页面头部 -->
        <PageHeader title="任务管理" icon="clipboard-list" />

        <!-- 统计卡片区域 -->
        <TaskStats :tasks="tasks" />

        <!-- 筛选和搜索区域 -->
        <TaskFilter v-model="criteriaData" @query="handleSearch" />

        <!-- 任务列表区域 -->
        <div class="task-list-section">
            <div class="list-header">
                <h3 class="list-title">任务列表</h3>
            </div>

            <!-- 任务列表 -->
            <div class="task-list">
                <div v-if="loading" class="loading-state">
                    <el-icon class="is-loading">
                        <Icon name="mdiLoading" />
                    </el-icon>
                    <span>加载中...</span>
                </div>

                <div v-else-if="tasks.length === 0" class="empty-state">
                    <Icon name="clipboard-list-outline" size="xlarge" class="empty-icon" />
                    <h3>暂无任务</h3>
                    <p>当前没有找到符合条件的任务</p>
                </div>

                <div v-else class="task-items">
                    <TaskItem v-for="task in tasks" :key="task._id" :task="task" />
                </div>
            </div>

            <!-- 分页 -->
            <div v-if="pager.total > 0" class="pagination-section">
                <el-pagination v-if="pager.total > 0" v-model:current-page="pager.page" v-model:page-size="pager.size"
                    :total="pager.total" :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChanged"
                    @current-change="fetchData"></el-pagination>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, type Ref } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from '@/components/mdiIicon/index.vue'
import PageHeader from '@/components/pageHeader/index.vue'
import TaskItem from './components/TaskItem.vue'
import TaskStats from './components/TaskStats.vue'
import TaskFilter from './components/TaskFilter.vue'
import request from '@/utils/request'



// 响应式数据
const loading = ref(false)
//分页信息
const pager = reactive({
    total: -1,
    page: 1,
    size: parseInt(localStorage.getItem('page-size-task') || '10')
})
//查询条件
const criteriaData: Ref<any> = ref({ sortBy: '_insertTime', sortType: 'desc' })
// 任务列表数据
const tasks = ref<any[]>([])


// 方法
const handleSearch = () => {
    pager.page = 1
    pager.total = -1
    fetchData()
}

function handleSizeChanged() {
    fetchData()

    //
    localStorage.setItem('page-size-task', '' + pager.size)
}

async function fetchData(reset: boolean = false) {
    if (reset) {
        pager.total = -1
    }

    const params = {
        page: pager.page,
        size: pager.size,
        total: pager.total,
        criteria: JSON.stringify(criteriaData.value || {})
    }

    try {
        loading.value = true
        const response = await request.get('/task/query', { params })
        // console.log('任务查询结果:', response.data)

        // 处理返回的数据
        if (response.data && response.data.list) {
            tasks.value = response.data.list
            pager.size = response.data.size
            pager.page = response.data.page
            pager.total = response.data.total
        } else {
            tasks.value = []
            pager.page = 1
            pager.total = -1
        }
    } catch (error) {
        console.error('获取任务数据失败:', error)
        ElMessage.error('获取任务数据失败')
    } finally {
        loading.value = false
    }
}
//进入自动查询
onMounted(() => handleSearch())

</script>

<style scoped>
.task-management-page {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background-color: var(--el-fill-color-extra-light);
    overflow: hidden;
    padding: 24px;
}

.task-management-page :deep(.page-header) {
    margin-bottom: 10px;
    border-radius: var(--el-border-radius-base);
    border: 1px solid var(--el-border-color-light);
    background: var(--el-bg-color);
}



/* 分页样式 */
.pagination-section {
    padding: 20px 24px;
    background: white;
    border-top: 1px solid var(--el-border-color-lighter);
    display: flex;
    justify-content: center;
}

/* 任务列表样式 */
.task-list-section {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
}

.list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.list-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}


.task-list {
    background: transparent;
    border-radius: 12px;
    overflow: hidden;
}

.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: var(--el-text-color-regular);
    background: white;
    border-radius: 12px;
    border: 1px solid var(--el-border-color-lighter);
    margin: 16px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: var(--el-text-color-regular);
    background: white;
    border-radius: 12px;
    border: 1px solid var(--el-border-color-lighter);
    margin: 16px;
}

.empty-icon {
    color: var(--el-text-color-placeholder);
    margin-bottom: 16px;
}

.empty-state h3 {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 500;
}

.empty-state p {
    margin: 0;
    font-size: 14px;
    color: var(--el-text-color-regular);
}

.task-items {
    /* max-height: 600px; */
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
}
</style>
