import { reactive, type Component, type Reactive } from "vue"
import type { AgentRuntime, DisplayResult, TreeNode } from "../agentRuntimeSupport"
import DefaultDisplay from './default/DefaultDisplay.vue'


export class BaseDisplayResult implements DisplayResult {

    protected agentRuntime: AgentRuntime
    //对应的树节点,可能为空
    protected treeNode: TreeNode | undefined = undefined
    //记录错误信息
    protected errors: string[] = []
    //标题
    protected title: string = ''

    constructor(agentRuntime: AgentRuntime) {
        this.agentRuntime = agentRuntime
    }

    getComp(): Component {
        return DefaultDisplay
    }

    push(message: any): boolean {
        // console.log('BaseDisplayResult push', message.type, this)
        if (message.type.endsWith('-start')) {
            this.handleStartMessage(message)
        } else if (message.type.endsWith('-end') || message.type.endsWith('-error')) {
            this.handleEndMessage(message)
        } else {
            this.handleOtherMessage(message)
        }
        return false
    }
    getTitle(): string {
        return this.title
    }
    setTreeNode(treeNode: TreeNode): void {
        this.treeNode = treeNode
    }
    getData(): any {
        return { errors: this.errors }
    }
    protected handleStartMessage(message: any): void {
        this.title = message.data || ''

    }


    protected handleEndMessage(message: any): void {

        //
        if (message.type.endsWith('-error')) {
            //  
            this.errors.push(message.data)
        }


    }

    protected handleOtherMessage(message: any): void {

    }

}

export class DefaultDisplayResult extends BaseDisplayResult {
    protected contents: string[] = []
    protected files: any[] = []

    constructor(agentRuntime: AgentRuntime) {
        super(agentRuntime)
    }



    getData(): any {
        return {
            contents: this.contents,
            files: this.files,
            errors: this.errors
        }
    }
    protected handleOtherMessage(message: any): void {
        if (message.type === '_data-content') {

            this.contents.push(message.data)
        } else if (message.type === '_file-upload') {
            this.files.push(JSON.parse(message.data))

        }
    }
}