package com.mttk.orche.agent.javaCode.support;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.StringUtil;

import java.util.List;
import java.util.ArrayList;

/**
 * Java代码生成和执行的上下文信息
 */
public class JavaCodeContext {

    /**
     * 代理参数
     */
    private final AgentParam para;

    /**
     * 事务ID
     */
    private final String transactionId;

    /**
     * 记录最后生成的代码
     */
    private String code;

    /**
     * 编译后的类
     */
    private Class<? extends JavaCodeBase> compiledClass;

    /**
     * 记录最后编译的错误
     */
    private Throwable compileError;

    /**
     * 记录最后执行的错误
     */
    private Throwable executeError;

    /**
     * 记录执行结果
     */
    private JavaCodeOutput result;

    /**
     * 输入文件列表
     */
    private List<AgentFile> inputFiles = new ArrayList<>();

    public JavaCodeContext(AgentParam para, String transactionId) throws Exception {
        this.para = para;
        this.transactionId = transactionId;
        // 解析inputFiles
        String inputFileNames = para.getRequest().getString("inputFileNames");
        if (StringUtil.isEmpty(inputFileNames)) {
            return;
        }
        String[] fileNames = inputFileNames.split(",");
        for (String fileName : fileNames) {
            AgentFile agentFile = AgentUtil.getAgentFileService(para.getContext()).get(para.getContext(), fileName);
            if (agentFile == null) {
                continue;
            }
            inputFiles.add(agentFile);
        }
    }

    public AgentParam getPara() {
        return para;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class<? extends JavaCodeBase> getCompiledClass() {
        return compiledClass;
    }

    public void setCompiledClass(Class<? extends JavaCodeBase> compiledClass) {
        this.compiledClass = compiledClass;
    }

    public Throwable getCompileError() {
        return compileError;
    }

    public void setCompileError(Throwable compileError) {
        this.compileError = compileError;
    }

    public Throwable getExecuteError() {
        return executeError;
    }

    public void setExecuteError(Throwable executeError) {
        this.executeError = executeError;
    }

    public JavaCodeOutput getResult() {
        return result;
    }

    public void setResult(JavaCodeOutput result) {
        this.result = result;
    }

    public List<AgentFile> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(List<AgentFile> inputFiles) {
        this.inputFiles = inputFiles;
    }

    @Override
    public String toString() {
        return "JavaCodeContext{" +
                "para=" + para +
                ", transactionId='" + transactionId + '\'' +
                ", code='" + code + '\'' +
                ", compiledClass=" + compiledClass +
                ", compileError=" + compileError +
                ", executeError=" + executeError +
                ", result=" + result +
                ", inputFiles=" + inputFiles +
                '}';
    }
}
