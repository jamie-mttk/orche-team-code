package com.mttk.orche.agent.javaCode.util;

import com.mttk.orche.agent.javaCode.support.JavaCodeContext;
import com.mttk.orche.agent.javaCode.support.JavaCodeOutput;
import com.mttk.orche.service.support.AgentFile;

public class GenerateResultUtil {

    public static String generateResult(JavaCodeContext javaCodeContext) throws Exception {
        JavaCodeOutput result = javaCodeContext.getResult();

        if (result != null) {
            return generateSuccessResult(result);
        } else {
            return generateErrorResult(javaCodeContext);
        }
    }

    private static String generateSuccessResult(JavaCodeOutput result) {
        StringBuilder sb = new StringBuilder();
        sb.append("代码执行成功！\n\n");

        if (result.getContent() != null && !result.getContent().trim().isEmpty()) {
            sb.append("执行结果：\n");
            sb.append(result.getContent());
            sb.append("\n\n");
        }

        if (result.getOutputFiles() != null && !result.getOutputFiles().isEmpty()) {
            sb.append("输出文件：\n");
            for (AgentFile file : result.getOutputFiles()) {
                sb.append("- 文件名：").append(file.getFileName()).append("\n");
                sb.append("  描述：").append(file.getDescription()).append("\n");
                sb.append("  大小：").append(file.getSize()).append(" 字节\n");
            }
        }

        return sb.toString();
    }

    private static String generateErrorResult(JavaCodeContext javaCodeContext) {
        if (javaCodeContext.getExecuteError() != null) {
            return "代码执行失败：\n" + javaCodeContext.getExecuteError().getMessage();
        } else if (javaCodeContext.getCompileError() != null) {
            return "代码编译失败：\n" + javaCodeContext.getCompileError().getMessage();
        } else {
            return "代码处理过程中发生未知错误";
        }
    }
}
