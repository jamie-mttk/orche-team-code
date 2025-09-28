package com.mttk.orche.agent.javaCode.support;

import java.util.ArrayList;
import java.util.List;
import com.mttk.orche.service.support.AgentFile;

/**
 * Java代码生成的输出结果Bean
 */
public class JavaCodeOutput {

    private String content;
    private List<AgentFile> outputFiles = new ArrayList<>();

    public JavaCodeOutput() {
    }

    public JavaCodeOutput(String content, List<AgentFile> outputFiles) {
        this.content = content;
        this.outputFiles = outputFiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<AgentFile> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(List<AgentFile> outputFiles) {
        this.outputFiles = outputFiles;
    }
}
