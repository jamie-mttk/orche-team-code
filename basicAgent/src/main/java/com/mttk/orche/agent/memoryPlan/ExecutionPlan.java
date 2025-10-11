package com.mttk.orche.agent.memoryPlan;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import com.mttk.orche.util.StringUtil;

/**
 * 执行计划容器
 */
public class ExecutionPlan {
    /** 思考过程 */
    private String thinking;

    /** 计划项列表 */
    private List<PlanItem> items;

    public ExecutionPlan() {
        this.items = new ArrayList<>();
        this.thinking = "";
    }

    /**
     * 添加计划项
     */
    public void addItem(PlanItem item) {
        this.items.add(item);
    }

    /**
     * 获取下一个待执行的任务
     */
    public PlanItem getNextPendingItem() {
        for (PlanItem item : items) {
            if (item.getStatus() == PlanItem.Status.NOT_START) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据任务ID查找计划项
     */
    public PlanItem findItemById(String id) {
        if (id == null) {
            return null;
        }
        for (PlanItem item : items) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据工具名称查找计划项
     */
    public PlanItem findItemByTool(String toolName) {
        for (PlanItem item : items) {
            if (item.getTool().equals(toolName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 更新指定任务状态
     */
    public void updateItemStatus(PlanItem item, PlanItem.Status status) {
        item.setStatus(status);
    }

    /**
     * 是否全部完成
     */
    public boolean isAllCompleted() {
        if (items.isEmpty()) {
            return false;
        }
        for (PlanItem item : items) {
            if (item.getStatus() != PlanItem.Status.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有任何失败的任务
     */
    public boolean hasFailedItem() {
        for (PlanItem item : items) {
            if (item.getResult() == PlanItem.Result.FAILED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 序列化为JSON字符串
     */
    public String toJson() {
        Document doc = new Document();
        doc.append("thinking", thinking);

        List<Document> itemDocs = new ArrayList<>();
        for (PlanItem item : items) {
            itemDocs.add(item.toJson());
        }
        doc.append("items", itemDocs);

        return doc.toJson();
    }

    /**
     * 生成易读的文本表示，供LLM理解
     */
    public String toReadableText() {
        StringBuilder sb = new StringBuilder();

        if (!StringUtil.isEmpty(thinking)) {
            sb.append("## 思考\n");
            sb.append(thinking).append("\n\n");
        }

        sb.append("## 任务列表\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append(i + 1).append(". ");
            sb.append(items.get(i).toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 清空所有NOT_START状态的任务（用于重新规划）
     */
    public void clearPendingItems() {
        items.removeIf(item -> item.getStatus() == PlanItem.Status.NOT_START);
    }

    // Getters and Setters

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public List<PlanItem> getItems() {
        return items;
    }

    public void setItems(List<PlanItem> items) {
        this.items = items;
    }
}
