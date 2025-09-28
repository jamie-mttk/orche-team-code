import { ref, type Component } from 'vue'
import URLExtractDisplay from './URLExtractDisplay.vue'

import { DefaultDisplayResult } from '../displayResultBase'



// URL显示实现
export class URLExtractDisplayResult extends DefaultDisplayResult {
    private urlData = {
        keyword: '', // 来自那个keyword
        title: '',
        url: '',
        snippet: '',
        date: '',
        position: 0,
        content: '',
        status: 'NOT_START', // 状态，缺省值为NOT_START
        size: 0 // 大小
    }

    getComp(): Component {
        return URLExtractDisplay
    }


    getData(): any {
        return this.urlData
    }

    getTitle(): string {
        return this.urlData.title
    }
    handleStartMessage(message: any): void {

        const data = JSON.parse(message.data)
        this.urlData = data

    }
    handleEndMessage(message: any): void {
        try {
            const data = JSON.parse(message.data)
            this.urlData = data
        } catch (error) {
            //兼容以前版本
            if (message.type == '_url-extract-error') {
                this.urlData.status = 'FAIL'
                this.urlData.content = message.data
            }
            else {
                this.urlData.status = 'SUCCESS'
                this.urlData.content = message.data
            }
        }
    }



}