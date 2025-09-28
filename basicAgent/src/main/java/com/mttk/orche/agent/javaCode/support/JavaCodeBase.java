package com.mttk.orche.agent.javaCode.support;

/**
 * Java代码生成器接口
 * 定义生成Java代码的必须实现的方法
 */
public interface JavaCodeBase {

    /**
     * 生成Java代码的核心方法
     * 
     * @param input  包含生成代码所需的所有输入参数
     * @param output 用于存储生成结果的输出对象
     * @throws Exception 生成过程中的异常
     */
    void call(JavaCodeInput input, JavaCodeOutput output) throws Exception;
}
