import { reactive, shallowRef, type Component, type Reactive, type Ref, isRef } from 'vue'
import { BaseDisplayResult, DefaultDisplayResult } from '../displayResultBase'
import LLMDisplay from './LLMDisplay.vue'
import type { AgentRuntime } from '../../agentRuntimeSupport'

// LLM显示实现
export class LLMDisplayResult extends DefaultDisplayResult {
    private requestData: string = ''
    private responseContent: string = ''
    private responseReasoning: string = ''
    private toolCalls: any[] = []


    constructor(agentRuntime: AgentRuntime) {
        super(agentRuntime)

    }

    getComp(): Component {
        return LLMDisplay
    }


    // protected handleStartMessage(message: any): void {
    //     const data = JSON.parse(message.data || {})
    //     this.title = data?.title || 'LLM处理'
    // }
    protected handleOtherMessage(message: any): void {
        if (message.type === '_llm-request') {
            this.requestData = message.data
        } else if (message.type === '_llm-response') {
            this.responseContent = message.data
        } else if (message.type === '_llm-response-delta') {
            try {
                // 解析JSON字符串
                const delta = JSON.parse(message.data)

                // 
                if (delta.choices && delta.choices[0] && delta.choices[0].delta) {
                    const choice = delta.choices[0].delta
                    if (choice.content) {
                        this.responseContent += choice.content
                    }
                    if (choice.reasoning_content) {
                        this.responseReasoning += choice.reasoning_content
                    }
                    // 处理tool_calls
                    if (choice.tool_calls && Array.isArray(choice.tool_calls)) {
                        choice.tool_calls.forEach((toolCall: any) => {

                            // 查找是否已存在相同index的工具调用
                            const existingIndex = this.toolCalls.findIndex(tc => tc.index === toolCall.index)
                            if (existingIndex >= 0) {
                                // 更新已存在的工具调用
                                const existing = this.toolCalls[existingIndex]

                                // 合并arguments
                                if (toolCall.function?.arguments !== null && toolCall.function?.arguments !== undefined) {
                                    if (existing.function?.arguments) {
                                        existing.function.arguments += toolCall.function.arguments
                                    } else {
                                        existing.function = existing.function || {}
                                        existing.function.arguments = toolCall.function.arguments
                                    }
                                }

                                // 合并其他字段
                                if (toolCall.id && !existing.id) {
                                    existing.id = toolCall.id
                                }
                                if (toolCall.function?.name && !existing.function?.name) {
                                    existing.function = existing.function || {}
                                    existing.function.name = toolCall.function.name
                                }
                                if (toolCall.type && !existing.type) {
                                    existing.type = toolCall.type
                                }
                            } else {
                                // 添加新的工具调用
                                this.toolCalls.push({
                                    index: toolCall.index,
                                    id: toolCall.id || '',
                                    type: toolCall.type || 'function',
                                    function: {
                                        name: toolCall.function?.name || '',
                                        arguments: toolCall.function?.arguments || ''
                                    }
                                })
                            }
                        })
                    }
                }
            } catch (error) {
                console.error('LLMDisplayResult handleOtherMessage error', error)
                // 如果解析失败，直接追加原始数据
                this.responseContent += message.data
            }
        } else {
            // console.log('@@@@@@@@@@@@@@@@@@LLMDisplayResult handleOtherMessage', message)
            super.handleOtherMessage(message)
        }
    }
    getData(): any {
        return {
            requestData: this.requestData,
            responseContent: this.responseContent,
            responseReasoning: this.responseReasoning,
            toolCalls: this.toolCalls,
            contents: this.contents
        }
    }
}