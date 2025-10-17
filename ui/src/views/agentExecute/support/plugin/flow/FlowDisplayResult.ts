import type { AgentRuntime } from '../../agentRuntimeSupport'
import { DefaultDisplayResult } from '../displayResultBase'

export class FlowDisplayResult extends DefaultDisplayResult {


    constructor(agentRuntime: AgentRuntime) {
        super(agentRuntime)
    }
    protected handleStartMessage(message: any): void {
        this.title = '流程执行'
    }
    protected handleEndMessage(message: any): void {
        if (message.type == '_flow-end') {

            const data = JSON.parse(message.data)
            if (data.files && Array.isArray(data.files)) {
                // console.log('flow-end files', data.files)
                this.files.push(...data.files)
            }

        } else if (message.type == '_flow-error') {
            //注意flow是可能堆栈里还有很多的其他节点数据
            // 1. 从底往上显示堆栈里所有数据
            const stackData = this.agentRuntime.levelStack.slice().reverse()
            // console.log('Flow stack data (from bottom to top):', stackData)

            // 2. 如果不为空获取堆栈最底部数据到 flowStartItem
            let flowStartItem = null
            if (this.agentRuntime.levelStack.length > 0) {
                flowStartItem = this.agentRuntime.levelStack[0] // 最底部数据
                // console.log('Flow start item (bottom of stack):', flowStartItem)
            }
        }
        //
        super.handleEndMessage(message)
    }
}