import type { AgentRuntime, DisplayResult } from '../agentRuntimeSupport'

// 导入各个功能的DisplayResult类

import { FlowDisplayResult } from './flow/FlowDisplayResult'
import { AgentDisplayResult } from './agent/AgentDisplayResult'
import { LLMDisplayResult } from './llm/LLMDisplayResult'
import { FuncDisplayResult } from './func/FuncDisplayResult'
import { URLExtractDisplayResult } from './urlExtract/URLExtractDisplayResult'

// 创建DisplayResult实例的工厂函数类型
type DisplayResultFactory = (agentRuntime: AgentRuntime) => DisplayResult

// 维护key到DisplayResult工厂函数的映射对象
const displayResultMap: Record<string, DisplayResultFactory> = {
    '_flow': (agentRuntime: AgentRuntime) => new FlowDisplayResult(agentRuntime),
    '_agent': (agentRuntime: AgentRuntime) => new AgentDisplayResult(agentRuntime),
    '_func': (agentRuntime: AgentRuntime) => new FuncDisplayResult(agentRuntime),
    '_llm': (agentRuntime: AgentRuntime) => new LLMDisplayResult(agentRuntime),
    '_url-extract': (agentRuntime: AgentRuntime) => new URLExtractDisplayResult(agentRuntime),
}

/**
 * 根据key查找对应的DisplayResult
 * @param key 显示类型key
 * @returns DisplayResult实例或undefined
 */
export function findManager(key: string, agentRuntime: AgentRuntime): DisplayResult | undefined {
    // 从映射对象中获取对应的工厂函数
    const factory = displayResultMap[key]

    if (factory) {
        return factory(agentRuntime)
    }

    // 如果没有找到对应的工厂函数，返回默认的DisplayResult
    //临时停止警告 - TBD
    // ElMessage.warning(`未找到对应的显示管理器: ${key}`)
    // return new DefaultDisplayResult()
    return undefined;
}

