package com.mttk.orche.agent.memoryPlan;

import org.bson.Document;
import com.mttk.orche.util.StringUtil;

/**
 * 执行计划中的单个任务项
 */
public class PlanItem {
    /**
     * 计划项状态枚举
     */
    public enum Status {
        NOT_START("Not start"),
        IN_PROGRESS("In progress"),
        COMPLETED("Completed");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 计划项执行结果枚举
     */
    public enum Result {
        NONE("None"),
        SUCCESS("Success"),
        FAILED("Failed");

        private final String description;

        Result(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /** 任务ID（任务项的唯一标识，如task1, task2等） */
    private String id;

    /** 任务描述 */
    private String name;

    /** 任务状态 */
    private Status status;

    /** 工具名称 */
    private String tool;

    /** 工具输入参数（JSON字符串） */
    private String inputParams;

    /** 执行结果 */
    private Result result;

    /** 成功返回值 */
    private String successOutput;

    /** 失败错误信息 */
    private String errorMessage;

    /**
     * 构造函数 - 创建新任务
     */
    public PlanItem(String id, String name, String tool) {
        this.id = id;
        this.name = name;
        this.tool = tool;
        this.status = Status.NOT_START;
        this.result = Result.NONE;
        this.inputParams = "";
        this.successOutput = "";
        this.errorMessage = "";
    }

    /**
     * 序列化为JSON对象
     */
    public Document toJson() {
        Document doc = new Document();
        doc.append("id", id);
        doc.append("name", name);
        doc.append("status", status.name());
        doc.append("tool", tool);
        doc.append("inputParams", inputParams);
        doc.append("result", result.name());
        doc.append("successOutput", successOutput);
        doc.append("errorMessage", errorMessage);
        return doc;
    }

    /**
     * 生成易读的Markdown文本表示，供LLM理解
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // 任务ID和名称
        sb.append("**").append(id).append("**: ").append(name);

        // 添加状态标识
        if (status != null && status != Status.NOT_START) {
            sb.append(" - *").append(status.getDescription()).append("*");
        }

        // 工具名称
        sb.append("\n  - Tool: `").append(tool).append("`");

        // 如果有输入参数，添加参数信息
        if (!StringUtil.isEmpty(inputParams) && !"{}".equals(inputParams)) {
            sb.append("\n  - Params: `").append(inputParams).append("`");
        }

        // 添加结果信息
        if (result != null && result != Result.NONE) {
            if (result == Result.SUCCESS && !StringUtil.isEmpty(successOutput)) {
                sb.append("\n  - Result: ");
                // 判断是否需要代码块包裹
                if (needsCodeBlock(successOutput)) {
                    sb.append("\n```\n").append(successOutput).append("\n```");
                } else {
                    sb.append(successOutput);
                }
            } else if (result == Result.FAILED && !StringUtil.isEmpty(errorMessage)) {
                sb.append("\n  - Error: ");
                if (needsCodeBlock(errorMessage)) {
                    sb.append("\n```\n").append(errorMessage).append("\n```");
                } else {
                    sb.append(errorMessage);
                }
            }
        }

        return sb.toString();
    }

    /**
     * 判断内容是否需要用代码块包裹
     * 如果是JSON、包含多行、或者很长，则需要代码块
     */
    private boolean needsCodeBlock(String content) {
        if (StringUtil.isEmpty(content)) {
            return false;
        }

        String trimmed = content.trim();

        // JSON格式
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return true;
        }

        // Markdown格式（包含#标题或代码块）
        if (trimmed.contains("##") || trimmed.contains("```")) {
            return true;
        }

        // 多行内容
        if (content.contains("\n")) {
            return true;
        }

        // 很长的内容（超过80字符）
        if (content.length() > 80) {
            return true;
        }

        return false;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSuccessOutput() {
        return successOutput;
    }

    public void setSuccessOutput(String successOutput) {
        this.successOutput = successOutput;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
