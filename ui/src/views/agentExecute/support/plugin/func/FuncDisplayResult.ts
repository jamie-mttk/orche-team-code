import type { AgentRuntime } from '../../agentRuntimeSupport'
import { DefaultDisplayResult } from '../displayResultBase'

export class FuncDisplayResult extends DefaultDisplayResult {


    constructor(agentRuntime: AgentRuntime) {
        super(agentRuntime)
    }


}