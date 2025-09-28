import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import config from '../config/env'

// 创建axios实例
const http = axios.create({
    baseURL: config.apiBaseURL,
    timeout: 300000, // 默认超时时间
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
http.interceptors.request.use(
    (config) => {
        // 可以在这里添加token等认证信息
        // const token = localStorage.getItem('token')
        // if (token) {
        //   config.headers.Authorization = `Bearer ${token}`
        // }

        // 处理超时设置
        if ((config as any).timeout !== undefined) {
            config.timeout = (config as any).timeout
        }

        // 显示全局加载状态（默认显示，除非明确设置为false）
        if ((config as any).showLoading !== false) {
            const loadingInstance = ElLoading.service({
                lock: true,
                text: '正在处理...',
                background: 'rgba(0, 0, 0, 0.7)'
            })
                // 将loading实例存储到config中，以便在响应拦截器中关闭
                ; (config as any).loadingInstance = loadingInstance
        }

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
http.interceptors.response.use(
    (response) => {
        // 关闭加载状态
        if ((response.config as any).loadingInstance) {
            (response.config as any).loadingInstance.close()
        }
        return response
    },
    (error) => {
        // 关闭加载状态
        if (error.config && (error.config as any).loadingInstance) {
            (error.config as any).loadingInstance.close()
        }

        // 统一错误处理
        let message = '请求失败'

        if (error.response) {
            switch (error.response.status) {
                case 400:
                    message = '请求参数错误'
                    break
                case 401:
                    message = '未授权，请重新登录'
                    break
                case 403:
                    message = '拒绝访问'
                    break
                case 404:
                    message = '请求地址不存在'
                    break
                case 500:
                    message = '服务器内部错误'
                    break
                default:
                    message = `连接错误${error.response.status}`
            }
        } else if (error.request) {
            message = '网络连接异常'
        }

        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default http
