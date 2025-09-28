package com.mttk.orche.agent.javaCode.support;

import java.util.ArrayList;
import java.util.List;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.service.support.AgentFile;

/**
 * Java代码生成的输入参数Bean
 */
public class JavaCodeInput {

    private AgentParam para;
    private String transactionId;
    private List<AgentFile> inputFiles = new ArrayList<>();

    public JavaCodeInput(AgentParam para, String transactionId, List<AgentFile> inputFiles) {
        this.para = para;
        this.transactionId = transactionId;
        this.inputFiles = inputFiles;
    }

    public AgentParam getPara() {
        return para;
    }

    public void setPara(AgentParam para) {
        this.para = para;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<AgentFile> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(List<AgentFile> inputFiles) {
        this.inputFiles = inputFiles;
    }
}
