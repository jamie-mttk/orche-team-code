package com.mttk.orche.agent.memoryPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.util.StringUtil;

/**
 * 执行计划容器
 */
public class ExecutionPlan {
    /** 思考过程 */
    private String thinking;

    /** 计划项列表 */
    private List<Task> tasks;

    public ExecutionPlan() {
        this.tasks = new ArrayList<>();
        this.thinking = "";
    }

    /**
     * 是否全部完成
     */
    public boolean isAllCompleted() {
        if (tasks.isEmpty()) {
            return false;
        }
        for (Task item : tasks) {
            if (item.getStatus() != Task.Status.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成易读的文本表示，供LLM理解
     */
    public String toReadableText() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ");
            sb.append(tasks.get(i).toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 根据MemoryPlanResponse更新ExecutionPlan
     * 
     * @param planResponse 响应对象
     * @throws Exception 处理异常
     */
    public void update(MemoryPlanResponse planResponse) throws Exception {
        // 更新thinking
        if (!StringUtil.isEmpty(planResponse.getThinking())) {
            this.thinking = planResponse.getThinking();
        }

        // 如果planResponse的tasks为空，不处理
        if (planResponse.getTasks() == null || planResponse.getTasks().isEmpty()) {
            return;
        }

        // 创建一个映射，保存原有tasks的状态信息（按id映射）
        Map<String, Task> existingTasksMap = new HashMap<>();
        for (Task task : this.tasks) {
            existingTasksMap.put(task.getId(), task);
        }

        // 用planResponse的tasks替换，但保留原有的状态信息
        List<Task> newTasks = new ArrayList<>();
        for (Task task : planResponse.getTasks()) {
            // 创建新的Task对象
            Task newTask = new Task(task.getId(), task.getName(), task.getTool());

            // 如果原有tasks中存在相同id的任务，保留其状态信息
            Task existingTask = existingTasksMap.get(task.getId());
            if (existingTask != null) {
                newTask.setStatus(existingTask.getStatus());
                newTask.setInputParams(existingTask.getInputParams());
                newTask.setSuccessOutput(existingTask.getSuccessOutput());
                newTask.setErrorMessage(existingTask.getErrorMessage());
            }

            newTasks.add(newTask);
        }

        // 替换tasks
        this.tasks = newTasks;
    }

    /**
     * 根据toolCall的id更新对应任务为成功完成状态
     * 
     * @param toolCall      工具调用对象
     * @param successOutput 成功输出
     */
    public void updateCall(ToolCall toolCall, String successOutput) {
        if (toolCall == null || toolCall.getId() == null) {
            return;
        }

        for (Task task : this.tasks) {
            if (toolCall.getId().equals(task.getId())) {
                task.setStatus(Task.Status.COMPLETED);
                task.setSuccessOutput(successOutput != null ? successOutput : "");
                break;
            }
        }
    }

    /**
     * 根据toolCall的id更新对应任务为失败状态
     * 
     * @param toolCall     工具调用对象
     * @param errorMessage 错误信息
     */
    public void updateCallError(ToolCall toolCall, String errorMessage) {
        if (toolCall == null || toolCall.getId() == null) {
            return;
        }

        for (Task task : this.tasks) {
            if (toolCall.getId().equals(task.getId())) {
                task.setStatus(Task.Status.FAILED);
                task.setErrorMessage(errorMessage != null ? errorMessage : "");
                break;
            }
        }
    }

    // Getters

    public String getThinking() {
        return thinking;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
