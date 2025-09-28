package com.mttk.orche.agent.webSearch.summary;

/**
 * 内容构建结果Bean
 * 包含当前索引和构建的内容
 */
public class ContentResult {

    /**
     * 当前处理到的索引位置
     */
    private int currentIndex;

    /**
     * 构建的内容
     */
    private String content;

    // 构造函数
    public ContentResult() {
    }

    public ContentResult(int currentIndex, String content) {
        this.currentIndex = currentIndex;
        this.content = content;
    }

    // Getter和Setter方法
    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ContentResult{" +
                "currentIndex=" + currentIndex +
                ", content='" + content + '\'' +
                '}';
    }
}
