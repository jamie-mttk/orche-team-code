import type { AgentRuntime } from '../../agentRuntimeSupport'
import { DefaultDisplayResult } from '../displayResultBase'


export class AgentDisplayResult extends DefaultDisplayResult {


    constructor(agentRuntime: AgentRuntime) {
        super(agentRuntime)
    }


    protected handleStartMessage(message: any): void {
        const data = JSON.parse(message.data || {})
        this.title = data?.name || '智能体执行'
        if (data?.request) {
            this.contents.push(JSON.stringify(data.request, null, 2))
        }
    }
    protected handleEndMessage(message: any): void {
        if (message.type.endsWith('-end')) {
            //  
            if (message.data) {
                this.contents.push(message.data)
            }
            //
            return
        }
        //
        super.handleEndMessage(message)
    }

}