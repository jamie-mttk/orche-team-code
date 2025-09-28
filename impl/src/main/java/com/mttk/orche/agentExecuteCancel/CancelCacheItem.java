package com.mttk.orche.agentExecuteCancel;

//记录是否取消检查的结果
class CancelCacheItem {
    private String sessionId;
    // 上次检查时间
    private long lastCheckTime;
    // 检查结果
    private boolean cancelRequested;

    public CancelCacheItem(String sessionId, boolean cancelRequested) {
        this.sessionId = sessionId;
        this.cancelRequested = cancelRequested;
        resetLastCheckTime();
    }

    //
    public String getSessionId() {
        return sessionId;
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public boolean isCancelRequested() {
        return cancelRequested;
    }

    public void resetLastCheckTime() {
        this.lastCheckTime = System.currentTimeMillis();
    }

}
