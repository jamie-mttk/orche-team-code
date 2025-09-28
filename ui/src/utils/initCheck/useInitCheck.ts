import { ref } from 'vue'
import { initAPI } from '@/api'
import { ElMessage } from 'element-plus'

export interface InitCheckResult {
    initFlag: boolean
}

// 全局状态，确保整个应用只检查一次
const globalState = {
    isChecking: ref(false),
    isInitialized: ref<boolean | null>(null),
    hasChecked: ref(false)
}

export function useInitCheck() {
    const { isChecking, isInitialized, hasChecked } = globalState

    const checkInit = async () => {
        if (hasChecked.value || isChecking.value) {
            return
        }

        isChecking.value = true
        hasChecked.value = true

        try {
            const response = await initAPI.checkInit()
            const result: InitCheckResult = response.data

            isInitialized.value = result.initFlag

            // 显示初始化检查结果
            if (result.initFlag) {
                ElMessage.success('系统已初始化完成')
            } else {
                ElMessage.warning('系统尚未初始化，请进行初始化配置')
            }

        } catch (error) {
            console.error('初始化检查失败:', error)
            ElMessage.error('初始化检查失败，请检查网络连接')
            isInitialized.value = false
        } finally {
            isChecking.value = false
        }
    }

    // 重置检查状态（用于重新检查）
    const resetCheck = () => {
        hasChecked.value = false
        isInitialized.value = null
    }

    return {
        isChecking,
        isInitialized,
        hasChecked,
        checkInit,
        resetCheck
    }
}
