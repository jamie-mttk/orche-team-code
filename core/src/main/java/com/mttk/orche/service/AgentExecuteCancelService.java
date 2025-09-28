package com.mttk.orche.service;

import com.mttk.orche.core.PersistService;

public interface AgentExecuteCancelService extends PersistService {
    public enum CANCEL_RESULT {
        NOT_FOUDND, // 请找到sessionId的记录
        INVALID, // sessionId对应的任务状态不是运行中
        REQUESTED, // 取消请求已经记录
        DUPLICATED,// 重复的取消请求
    }

    public CANCEL_RESULT cancel(String sessionId) throws Exception;

    public boolean isCancelRequested(String sessionId) throws Exception;

}
