package com.mttk.orche.agentExecuteCancel;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.AgentExecuteCancelService;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.support.ServerUtil;

@ServiceFlag(key = "agentExecuteCancelService", name = "智能体执行取消管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentExecuteCancelServiceImpl extends AbstractPersistService implements AgentExecuteCancelService {
    //
    private static final long CANCEL_RECHECK_TIME = 1000 * 2; // 是否cancel检查间隔 - 2s
    private static final long CANCEL_CHECK_EXPIRE = 1000 * 60 * 5; // 从cancelChcekCache中删除过期的数据
    private static final long CANCEL_REQUEST_EXPIRE = 1000 * 60 * 15; // 从数据库中删除过期的请求记录
    // 缓存 - 记录是否取消检查的结果
    private Map<String, CancelCacheItem> cancelChcekCache = new ConcurrentHashMap<>();
    // 定时任务执行器
    private ScheduledExecutorService scheduledExecutor;

    @Override
    public CANCEL_RESULT cancel(String sessionId) throws Exception {
        Optional<Document> o1 = ServerUtil.getService(AgentExecuteService.class).load("sessionId", sessionId);
        if (o1.isEmpty()) {
            return CANCEL_RESULT.NOT_FOUDND;
        }
        Document task = o1.get();
        if (!"running".equals(task.get("status"))) {
            return CANCEL_RESULT.INVALID;
        }
        //
        Optional<Document> o2 = load("sessionId", sessionId);
        if (o2.isPresent()) {
            return CANCEL_RESULT.DUPLICATED;
        }
        //
        Document cancelDocument = new Document();
        cancelDocument.append("sessionId", sessionId);
        cancelDocument.append("cancelTime", new Date());
        save(cancelDocument);
        //
        CancelCacheItem cancelCacheItem = new CancelCacheItem(sessionId, true);
        cancelChcekCache.put(sessionId, cancelCacheItem);
        //
        return CANCEL_RESULT.REQUESTED;
    }

    @Override
    public boolean isCancelRequested(String sessionId) throws Exception {
        CancelCacheItem cancelCacheItem = cancelChcekCache.get(sessionId);
        if (cancelCacheItem != null) {
            if (System.currentTimeMillis() - cancelCacheItem.getLastCheckTime() < CANCEL_RECHECK_TIME) {
                // 重置检查事件防止被清理
                cancelCacheItem.resetLastCheckTime();
                //
                return cancelCacheItem.isCancelRequested();
            } else {
                // 和缓存不存在一样,从数据库获取
            }
        }
        //
        Optional<Document> o = load("sessionId", sessionId);
        // 记录
        CancelCacheItem cancelCacheItem2 = new CancelCacheItem(sessionId, o.isPresent());
        cancelChcekCache.put(sessionId, cancelCacheItem2);
        //
        return o.isPresent();
    }

    /**
     * 清理过期的检查缓存
     * 检查cancelChcekCache里所有的CancelCacheItem
     * 如果CancelCacheItem的getLastCheckTime时间超过CANCEL_CHECK_EXPIRE则从cancelChcekCache里删除
     */
    private void removeExpiredCheckCache() {
        long currentTime = System.currentTimeMillis();
        cancelChcekCache.entrySet().removeIf(entry -> {
            CancelCacheItem item = entry.getValue();
            return currentTime - item.getLastCheckTime() > CANCEL_CHECK_EXPIRE;
        });
    }

    // 删除过期的请求记录,否则会移植在数据库里
    private long removeExpiredRequestCache() {
        return this.obtainCollection()
                .deleteMany(Filters.lt("cancelTime", new Date(System.currentTimeMillis() - CANCEL_REQUEST_EXPIRE)))
                .getDeletedCount();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        // 创建定时任务执行器
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "AgentExecuteCancel-CleanupTask");
            thread.setDaemon(true);
            return thread;
        });

        // 启动定时任务，每隔1分钟执行一次
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                removeExpiredCheckCache();
                removeExpiredRequestCache();
            } catch (Exception e) {
                // 记录错误但不中断定时任务
                System.err.println("定时清理任务执行失败: " + e.getMessage());
                e.printStackTrace();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    protected void doStop() throws Exception {
        // 停止定时任务
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
            try {
                // 等待最多5秒让正在执行的任务完成
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        super.doStop();
    }
}
