import type { Component, Ref } from 'vue'
import { ref, computed } from 'vue'

import { processMessageInputFunc } from './agentRuntimeProcessMessage'
import { callApiInternal } from './agentRuntimeShare'
import { agentAPI, agentTemplateAPI } from '@/api'
import { ElMessage } from 'element-plus'
import { createUniqueString } from '@/utils/tools'
// DisplayResult接口定义
export interface DisplayResult {
    // 获取Vue组件
    getComp(): Component
    // 处理消息 返回true说明跳过标准处理
    push(message: any): boolean
    // 获取处理后的数据
    getData(): any
    //得到标题,必须在-start消息处理后设置
    getTitle(): string
    //设置相关树节点
    setTreeNode(treeNode: TreeNode): void
}


// 执行状态枚举
export const ExecutionStatus = {
    IDLE: 'idle',           // 未执行
    EXECUTING: 'executing', // 执行中
    STOPPING: 'stopping'    // 等待停止
} as const

export type ExecutionStatusType = typeof ExecutionStatus[keyof typeof ExecutionStatus]

// 树节点数据结构
export interface TreeNode {
    id: string
    type: string
    status: 'processing' | 'failed' | 'success'
    displayResult: DisplayResult
    label: string
    startTime: number | undefined   //开始执行时间
    endTime: number | undefined     //结束执行时间
    children: TreeNode[]
}

// Agent运行时状态
export class AgentRuntime {
    public status: Ref<ExecutionStatusType>     //当前执行状态
    public treeNodes: Ref<TreeNode[]>           //树的节点
    private selectedNode: Ref<TreeNode | null>  //当前选中的节点（私有）
    public sessionId: Ref<string>              //会话ID
    public agentId: Ref<string>               //智能体ID
    public agent: Ref<any | null>              //智能体对象
    public levelStack: TreeNode[]         //层级栈
    public agentTemplate: Ref<any> //智能体模板
    public agentInputConfig: Ref<any> //智能体输入配置
    public ready: Promise<void>              // 新增初始化完成标志
    public autoSwitchNode: Ref<boolean>        //是否自动切换节点
    private messageFunc: (message: any) => void


    constructor(
        agentId: string
    ) {
        this.agentId = ref(agentId)

        this.sessionId = ref('')

        //
        this.selectedNode = ref(null)
        this.status = ref(ExecutionStatus.IDLE)
        this.treeNodes = ref([])
        this.agent = ref({})
        this.levelStack = []
        this.agentTemplate = ref({})
        this.agentInputConfig = ref({})
        this.autoSwitchNode = ref(true)  //默认自动切换节点
        //
        this.messageFunc = processMessageInputFunc(this)
        //
        // 初始化异步任务
        this.ready = this.initialize()
    }

    public sendRequestInput(requestData: any): void {
        //每次执行生成唯一的sessionId
        this.sessionId.value = createUniqueString()
        //
        this.status.value = ExecutionStatus.IDLE
        this.treeNodes.value = []
        this.setSelectedNode(null, true)  //初始化时强制设置为null
        this.levelStack = []
        //        
        //
        callApiInternal(this, 'execute', requestData)
        // sendRequestInputInternal(this, requestData)
    }
    public async replay(sessionId: string) {

        this.sessionId = ref(sessionId)
        //
        callApiInternal(this, 'replay', {})
    }
    public sendMessage(message: any): void {
        this.messageFunc(message)
    }

    // 初始化异步任务
    private async initialize() {
        // 
        await this.loadAgent()
        //
        await this.loadAgentTemplate()
    }

    // 加载智能体数据
    private async loadAgent() {
        try {
            const response = await agentAPI.getAgent(this.agentId.value)
            this.agent.value = response.data

        } catch (error) {
            console.error('加载智能体失败:', error)
            ElMessage.error('加载智能体失败')
        }
    }
    //加载智能体模板和输入配置(做了调整)
    private async loadAgentTemplate() {
        if (!this.agent.value?.agentTemplate) {
            return
        }
        //
        try {
            const response = await agentTemplateAPI.getTemplateByKey(this.agent.value?.agentTemplate)
            if (response.data) {
                this.agentTemplate.value = response.data
                //
                const config = response.data.ui_call
                //
                if (!config.props) {
                    config.props = {}
                }
                config.props.hideRequiredAsterisk = true
                config.props.labelWidth = '4px'
                config.props.helpMode = 'placeholder'
                config.props.size = "default"

                //

                //
                for (let item of config.children || []) {
                    //如果有label,但是没有description,用label设置description
                    if (item.label && !item.description) {
                        item.description = item.label
                    }
                    //删除label,否则显示会错位
                    delete item.label
                    if (!item.props) {
                        item.props = {}
                    }
                    item.props.showLabel = false

                    //如果是select/chooser,缺省不添加_NAME
                    if ('select' == item.mode) {
                        if (!item.props) {
                            item.props = {}
                        }
                        if (item.props.appendSelectLabel == undefined) {
                            item.props.appendSelectLabel = false
                        }
                    }

                }
                //
                this.agentInputConfig.value = config
            }
        } catch (error) {
            console.error('加载智能体模板配置失败:', error)
            ElMessage.error('加载智能体模板配置失败')
        }
    }

    /**
     * 设置选中节点
     * @param newNode 新的节点
     * @param force 是否强制设置，默认为false。如果为true，忽略autoSwitchNode的值
     */
    public setSelectedNode(newNode: TreeNode | null, force: boolean = false): void {
        // 只有在autoSwitchNode为true或force为true时才修改节点
        if (this.autoSwitchNode.value || force) {
            this.selectedNode.value = newNode
        }
    }

    /**
     * 获取当前选中节点
     */
    public getSelectedNode(): TreeNode | null {
        return this.selectedNode.value
    }

    /**
     * 获取当前选中节点的key（用于树组件的current-node-key）
     */
    public getSelectedNodeKey = computed(() => {
        return this.selectedNode.value?.id || null
    })
}

// 使用工厂函数创建实例
export async function createAgentRuntime(agentId: string): Promise<AgentRuntime> {
    const instance = new AgentRuntime(agentId);
    await instance.ready;
    return instance;
}