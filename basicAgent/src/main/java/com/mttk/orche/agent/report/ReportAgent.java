package com.mttk.orche.agent.report;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;

import com.mttk.orche.agent.report.util.ReportGenerator;
import com.mttk.orche.agent.report.util.ReportGeneratorFacatory;

@AgentTemplateFlag(key = "_report-agent", name = "报告生成助手", description = "根据输入文件列表和任务生成汇总报告,保存到输出文件里.PPT格式为HTML文件,后缀使用HTML.调用此工具前 **输入文件列表inputFileNames** 必须存在,否则调用 **上下文汇总助手**生成后再调用本工具.", callDefineClass = ReportCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })

public class ReportAgent extends AbstractAgent {
    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        AgentContext context = para.getContext();
        AdapterConfig config = para.getConfig();
        AdapterConfig request = para.getRequest();

        //
        ReportGenerator reportGenerator = ReportGeneratorFacatory.build(request);
        String content = reportGenerator.generate(context, config, request);
        //
        // String fileName = FileToolUtil.removeSpecialChars( "生成的报告_" +
        // StringUtil.getUUID() + "的最终结果." + reportGenerator.getFileExt());
        String outputFileName = request.getString("outputFileName");
        //
        AgentUtil.getAgentFileService(context).upload(context, outputFileName, request.getString("fileDescription"),
                content);

        //
        StringBuilder sb = new StringBuilder(1024);
        sb.append("针对 ").append(request.getString("fileDescription")).append(" 的报告已经生成并保存到文件 ")
                .append(outputFileName)
                .append(" 中.");
        //
        return sb.toString();
    }

}
