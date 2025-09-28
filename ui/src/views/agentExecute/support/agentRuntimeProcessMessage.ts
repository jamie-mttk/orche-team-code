import { ElMessage } from "element-plus";
import { reactive } from "vue";
import moment from 'moment'
import { findManager } from "./plugin/displayResultFactory";
import type { AgentRuntime, TreeNode } from "./agentRuntimeSupport"
//创建一个函数是为了能够保持独立的变量
export const processMessageInputFunc = function (agentRuntime: AgentRuntime) {

    // 消息队列
    const messageQueue: any[] = []

    // 是否正在处理消息
    let isProcessing: boolean = false

    //
    return function (message: any) {
        // 将消息添加到队列
        messageQueue.push(message)

        // 如果当前没有在处理消息，开始处理队列
        if (!isProcessing) {
            processQueue()
        }
    }
    //
    function processQueue() {
        if (isProcessing || messageQueue.length === 0) {
            return
        }
        //
        isProcessing = true


        while (messageQueue.length > 0) {


            const message = messageQueue.shift()
            try {
                handleSingleMessage(message)
            } catch (error) {
                //处理失败,继续处理下一个
                console.error('处理消息失败:', error, message)

            } finally {
                isProcessing = false
            }
        }


        isProcessing = false
    }
    //
    function handleSingleMessage(message: any) {

        try {
            // 
            if (!message.type) {
                ElMessage.error('消息类型为空' + JSON.stringify(message))
                return
            }
            //忽略掉心跳消息
            if ('keep-alive' == message.type) {
                return
            }
            // console.log('message', message)
            // if (!message.type.startsWith('_flow')) {
            //     return
            // }
            if (message.type.endsWith('-start')) {

                // 处理开始消息
                handleStartMessage(message)
            } else if (message.type.endsWith('-end') || message.type.endsWith('-error')) {
                // console.log('结束消息',JSON.stringify(message))
                // 处理结束消息
                handleEndMessage(message)
            } else {
                // 处理其他消息
                handleOtherMessage(message)
            }
        } catch (error) {

            ElMessage.error(`处理消息失败: ${message.type}`)
            console.error('处理消息失败', { message, error, agentRuntime })
            throw error
        }
    }

    // 处理开始消息
    function handleStartMessage(message: any) {
        // 获取消息类型前缀（去掉-start后缀）
        const messageType = message.type

        const key = messageType.substring(0, messageType.length - '-start'.length)
        const displayResult = findManager(key, agentRuntime)

        if (!displayResult) {
            ElMessage.error(`未找到对应的显示管理器：${key}`)
            return
        }

        // 先推送开始消息，让实现类有机会设置标题/初始化数据
        if (displayResult.push(message)) {
            return
        }


        //构造节点 - 使用 reactive 包装使其响应式
        const treeNode: TreeNode = reactive({
            id: message.id,
            type: key,
            status: 'processing',
            displayResult: displayResult,
            label: displayResult.getTitle(),
            children: [],
            startTime: message.sendTime || moment().valueOf(),
            endTime: undefined
        })
        displayResult.setTreeNode(treeNode)
        //
        let topNode = undefined
        if (agentRuntime.levelStack.length > 0) {
            topNode = agentRuntime.levelStack[agentRuntime.levelStack.length - 1]
            topNode.children.push(treeNode)
            //
            if (topNode && topNode.type != '_func') {
                //注意如果上级为_func,不入栈,因为_func下的子节点可能时并发执行的   
                agentRuntime.levelStack.push(treeNode)
            }
        } else {
            agentRuntime.treeNodes.value.push(treeNode)
            // 树节点数据push到levelStack中
            agentRuntime.levelStack.push(treeNode)
        }
        //
        // 自动选择新创建的节点，显示其详情
        agentRuntime.selectedNode.value = treeNode
    }
    function handleEndMessage(message: any) {
        const { isError, isFuncSpecial, thisNode } = handlePrecheck(message, 'end')
        if (isError || !thisNode) {
            return
        }
        //
        thisNode.endTime = message.sendTime || moment().valueOf()
        //
        if (thisNode.displayResult.push(message)) {
            return
        }
        //
        if (message.type.endsWith('-error')) {
            thisNode.status = 'failed'
        } else {
            thisNode.status = 'success'
        }
        if (!isFuncSpecial) {
            // levelStack顶部数据出栈
            agentRuntime.levelStack.pop()
        }
        //
        agentRuntime.selectedNode.value = thisNode


    }


    function handleOtherMessage(message: any) {
        const { isError, thisNode } = handlePrecheck(message, 'error')
        if (isError || !thisNode) {
            return
        }


        // 获得levelStack顶部的displayResult，把当前消息作为参数调用displayResult的push方法
        thisNode.displayResult.push(message)

        // 更新节点标签（可能因为消息处理而改变）
        // thisNode.label = thisNode.displayResult.getTitle()

        // 如果当前选中的就是这个节点，自动更新显示
        // if (selectedNode.value && selectedNode.value.id === message.id) {
        //     // 触发响应式更新
        //     selectedNode.value = { ...selectedNode.value }
        // }
    }

    //结束和出错消息预处理检查
    function handlePrecheck(message: any, operation: string) {
        if (agentRuntime.levelStack.length === 0) {
            ElMessage.error(`堆栈为空，无法执行${operation}操作`)
            return { isError: true }
        }
        if (message.type == '_flow-error') {
            //flow-error不一样,需要忽略中间的所有可能堆栈结果
            const flowNode = agentRuntime.levelStack[0]
            return { isError: false, topNode: flowNode, isFuncSpecial: false, thisNode: flowNode }
        }

        // 更新levelStack顶部节点状态为成功
        const topNode = agentRuntime.levelStack[agentRuntime.levelStack.length - 1]
        //true - 代表当前结束节点可能时
        const isFuncSpecial = topNode && topNode.type == '_func' && topNode.id != message.id
        //当前消息对应的节点
        let thisNode = topNode
        if (isFuncSpecial) {
            //试图从topNode的children中找到id为message.id的节点
            const child = topNode.children.find((child: TreeNode) => child.id === message.id)
            if (child) {
                thisNode = child
            } else {
                ElMessage.error(`未找到对应的节点：${message.id} - ${JSON.stringify(message)}`)
                return { isError: true }
            }
        } else {
            if ('end' == operation && topNode.id != message.id) {
                ElMessage.error(`节点ID与堆栈顶端不匹配：${message.id} - ${JSON.stringify(message)}`)
                return { isError: true }
            }
        }
        //
        return { isError: false, topNode, isFuncSpecial, thisNode }
    }
}