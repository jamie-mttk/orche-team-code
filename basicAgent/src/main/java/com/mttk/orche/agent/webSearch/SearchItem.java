package com.mttk.orche.agent.webSearch;

import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.util.StringUtil;

/**
 * 搜索结果项 Bean
 */
public class SearchItem {

    /**
     * 状态枚举
     */
    public enum STATUS {
        NOT_START,
        SUCCESS,
        FAIL,
        IGNORE
    }

    private String keyword; // 来自那个keyword
    private String title;
    private String url;
    private String snippet;
    private String date;
    private int position;
    private String content;
    private STATUS status = STATUS.NOT_START; // 状态，缺省值为NOT_START
    private int size; // 大小
    private int token;// 占用token数量,预估值

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (StringUtil.isEmpty(content)) {
            this.size = 0;
            this.token = 0;
        } else {
            this.size = content.length();
            this.token = AgentUtil.estimateTokenCount(content);
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

}
