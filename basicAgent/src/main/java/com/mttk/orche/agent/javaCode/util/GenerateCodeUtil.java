package com.mttk.orche.agent.javaCode.util;

import java.util.HashMap;
import java.util.Map;

import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.agent.javaCode.Prompt;
import com.mttk.orche.agent.javaCode.support.JavaCodeContext;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

public class GenerateCodeUtil {

    // 生成java代码
    public static void generateCode(JavaCodeContext javaCodeContext) throws Exception {
        //
        String prompt = generatePromote(javaCodeContext);
        //
        String response = AgentUtil.callLlm(javaCodeContext.getPara(), "生成代码", prompt);
        //
        // 去掉可能的markdown代码块标记
        response = removeMarkdownCodeBlocks(response);
        //
        javaCodeContext.setCode(response);
        // 返回
        JavaCodeUtil.sendMessage(javaCodeContext, response);
    }

    private static String generatePromote(JavaCodeContext javaCodeContext) throws Exception {
        // 构建替换参数
        Map<String, Object> env = new HashMap<>();
        env.put("task", javaCodeContext.getPara().getRequest().getString("task"));
        env.put("inputFileNames", genInputFileNames(javaCodeContext));
        env.put("code", javaCodeContext.getCode());
        if (javaCodeContext.getCompileError() != null) {
            env.put("compileError", ThrowableUtil.dump2String(javaCodeContext.getCompileError()));
        }
        if (javaCodeContext.getExecuteError() != null) {
            env.put("executeError", ThrowableUtil.dump2String(javaCodeContext.getExecuteError()));
        }
        //
        System.out.println("env 1:\n" + javaCodeContext);
        System.out.println("env 2:\n" + env);
        //
        String prompt = Prompt.PROMPT_JAVA_CODE;
        prompt = PromptUtil.parsePrompt(prompt, javaCodeContext.getPara(), env);
        return prompt;
    }

    //
    private static String genInputFileNames(JavaCodeContext javaCodeContext) throws Exception {

        if (javaCodeContext.getInputFiles() == null || javaCodeContext.getInputFiles().size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(1024);
        for (AgentFile agentFile : javaCodeContext.getInputFiles()) {

            sb.append("###").append(agentFile.getFileName()).append("\n");
            sb.append("#### 描述\n").append(agentFile.getDescription()).append("\n");
            sb.append("#### 大小\n").append(agentFile.getSize()).append("\n");
            // sb.append("#### 内容\n```\n").append(
            // AgentUtil.getAgentFileService(context).download(context,
            // agentFile.getFileName()))
            // .append("\n```\n");
        }
        //
        return sb.toString();
    }

    /**
     * 去掉可能的markdown代码块标记
     * 
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String removeMarkdownCodeBlocks(String content) {
        if (StringUtil.isEmpty(content)) {
            return content;
        }

        // 去掉开头的```java或```
        content = content.replaceAll("^\\s*```(?:java)?\\s*\\n?", "");

        // 去掉结尾的```
        content = content.replaceAll("\\n?\\s*```\\s*$", "");

        return content;
    }

}
