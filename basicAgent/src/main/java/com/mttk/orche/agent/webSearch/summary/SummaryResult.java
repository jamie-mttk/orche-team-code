package com.mttk.orche.agent.webSearch.summary;

/**
 * 汇总结果Bean
 * 对应REASONING_AND_SUMMARIZE提示词的返回结果
 */
public class SummaryResult {

    /**
     * 是否完整回答
     * 1: 完整, 0: 需要更多信息
     */
    private int isAnswer;

    /**
     * 待扩展检索的具体信息
     * 当isAnswer=0时提供
     */
    private String rewriteQuery;

    /**
     * 简要说明评估原因
     */
    private String reason;

    /**
     * Markdown格式的详细报告
     * 始终提供,基于现有内容生成
     */
    private String summarize;

    // 构造函数
    public SummaryResult() {
    }

    public SummaryResult(int isAnswer, String rewriteQuery, String reason, String summarize) {
        this.isAnswer = isAnswer;
        this.rewriteQuery = rewriteQuery;
        this.reason = reason;
        this.summarize = summarize;
    }

    // Getter和Setter方法
    public int getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(int isAnswer) {
        this.isAnswer = isAnswer;
    }

    public String getRewriteQuery() {
        return rewriteQuery;
    }

    public void setRewriteQuery(String rewriteQuery) {
        this.rewriteQuery = rewriteQuery;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSummarize() {
        return summarize;
    }

    public void setSummarize(String summarize) {
        this.summarize = summarize;
    }

    @Override
    public String toString() {
        return "SummaryResult{" +
                "isAnswer=" + isAnswer +
                ", rewriteQuery='" + rewriteQuery + '\'' +
                ", reason='" + reason + '\'' +
                ", summarize='" + summarize + '\'' +
                '}';
    }
}
