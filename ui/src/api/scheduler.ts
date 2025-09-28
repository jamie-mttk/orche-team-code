import http from '../utils/request'

// 定时任务相关API调用方法
export const schedulerAPI = {
    /**
     * 查询定时任务列表
     * GET /scheduler/listJobs
     * 返回值 list里是定时任务数组
     */
    listJobs: () => {
        return http.get('/scheduler/listJobs')
    },

    /**
     * 取消定时任务
     * POST /scheduler/cancelJob
     * 参数: { jobName: string, jobGroup: string }
     */
    cancelJob: (data: { jobName: string; jobGroup: string }) => {
        return http.post('/scheduler/cancelJob', data)
    }
}
