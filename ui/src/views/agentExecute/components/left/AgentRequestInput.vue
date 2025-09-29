<template>
    <div class="prompt-input">
        <el-card class="chat-card">
            <template #header>
                <div class="card-header">
                    <h3>请求信息</h3>
                </div>
            </template>
            <div class="content">
                <div class="input-section">
                    <el-input v-model="taskName" placeholder="自定义任务名称,没有使用智能体名称" maxlength="120" show-word-limit />

                    <!-- <div class="switch-container">
                        <el-switch v-model="logTask" />
                        <span class="switch-label">是否记录运行过程</span>
                    </div> -->
                </div>

                <div class="editor-container">
                    <CdForm ref="inputFormRef" :config="props.agentRuntime.agentInputConfig.value" :data="formData" />
                </div>
                <div class="actions">
                    <el-button v-if="isIdle" type="primary" size="large" @click="handleSend">
                        开始执行
                    </el-button>
                    <el-button v-if="isIdle" type="primary" size="large" @click="handleSchedule">
                        定时执行
                    </el-button>
                    <el-button v-if="isExecuting || isStopping" type="danger" size="large" @click="handleStop"
                        :loading="isStopping" :disabled="false">
                        停止执行
                    </el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import CdForm from '@/components/ConfigDyna/Form.vue'
import { type AgentRuntime } from '../../support/agentRuntimeSupport'
import request from '@/utils/request'
import { app } from '@/main'
import { dynamicRender } from 'mttk-vue-wrap'
import TaskScheduleDialog from '../scheduler/TaskScheduleDialog.vue'
import { stopTaskExecution } from './chatUtil'

// Props
interface Props {
    agentRuntime: AgentRuntime
}

const props = defineProps<Props>()



// 表单数据
const formData = ref({})
// const formData = ref({ 'query': '获取数据后进行3阶多项式回归模型,重点展示拟合值和检测值的关系.生成CSV文件就可以,禁止生成报告.\n自变量: 上游水位,下游水位,温度,检测日期\n因变量: 杨压力\n孔号: 节2-1 \n结果发送到jamie0828@163.com' })
// const formData = ref({ 'query': '获取数据后进行3阶多项式回归模型,重点展示拟合值和检测值的关系.生成HTML报告.\n自变量: 上游水位,下游水位,温度,检测日期\n因变量: 杨压力\n孔号: 节2-1' })
// const formData = ref({ 'query': '规划一个明年希望进入英国G5的学生的申请计划,该学生学习A-level,包括 数学/物理/化学和高等数学,目标专业数理统计,使用markdown格式,重点展示申请计划' })
// const formData = ref({
//     'query': '规划一个中国西藏旅行计划,以Markdown的方式给出报告并邮件给 jamie0828@163.com\n从上海出发,结束后回到上海\n时间:2025年9月19到9月25日\n要求覆盖 林芝,波密,拉萨,羊湖\n方式: 到了西藏租车自驾游\n喜好: 自然风光为主'
// })
// const formData = ref({ 'query': '查询从上海前往西藏（拉萨）的航班信息及进藏后租车服务推荐 ' })
// const formData = ref({ 'query': '规划去坦桑尼亚25天旅游,看动物迁徙与当地人人文,包含酒店,路程规划,费用,景点 景点等使用互联网搜索工具' })
//
const taskName = ref('')
// const logTask = ref(true)
// 表单引用
const inputFormRef = ref<FormInstance>()

// 计算属性 - 状态判断
const isIdle = computed(() => props.agentRuntime.status.value === 'idle')
const isExecuting = computed(() => props.agentRuntime.status.value === 'executing')
const isStopping = computed(() => props.agentRuntime.status.value === 'stopping')



// 发送消息
const handleSend = async () => {
    try {
        // 验证基本信息表单
        await inputFormRef.value?.validate()
        // 提交数据
        if (props.agentRuntime.sessionId.value) {
            ElMessageBox.confirm(
                '存在已经执行完成或失败的会话,是否启动新会话?',
                '确认继续',
                {

                    type: 'warning',
                }
            )
                .then(() => {
                    handleSendInternal()
                })
        } else {
            handleSendInternal()
        }

    } catch (error) {
        // 验证失败，显示错误信息
        console.error('请检查表单填写是否正确', error)
        ElMessage.error('请检查表单填写是否正确')
    }
}
const handleSendInternal = () => {
    props.agentRuntime.sendRequestInput({ taskName: taskName.value || props.agentRuntime.agent.value?.name, request: JSON.stringify(formData.value) })
}
const handleSchedule = async () => {
    await inputFormRef.value?.validate()
    //
    const visible = ref(false)
    //
    //
    const config = {
        '~': TaskScheduleDialog,
        '~modelValue': visible,
        '@saved': function (_: any, dataNew: any) {
            handleScheduleDone(dataNew)
        }
    }
    dynamicRender(config, app._context, {
        removeEvent: 'close'
    })

    //
    visible.value = true
}
const handleScheduleDone = async (schedulerData: any) => {
    const body = { taskName: taskName.value || props.agentRuntime.agent.value?.name, request: JSON.stringify(formData.value), agentId: props.agentRuntime.agentId.value, 'scheduler': JSON.stringify(schedulerData) }

    await request.post(`/chat/scheduler`, body)
    ElMessage.success('定时任务创建成功!请在定时任务里查看或停止.')

}

// 停止执行
const handleStop = async () => {
    await stopTaskExecution(props.agentRuntime.sessionId.value)
}


</script>

<style scoped>
.prompt-input {
    height: 100%;
}

.chat-card {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.card-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.content {

    display: flex;
    flex-direction: column;
}

.input-section {
    background: var(--el-fill-color-lighter);
    border: 1px solid var(--el-border-color-light);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.switch-container {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;
}

.switch-label {
    font-size: 14px;
    color: var(--el-text-color-regular);
}

.editor-container {
    flex: 1;
}

.actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

/* 加载状态样式 */
.actions .el-button {
    min-width: 120px;
    height: 40px;
}
</style>