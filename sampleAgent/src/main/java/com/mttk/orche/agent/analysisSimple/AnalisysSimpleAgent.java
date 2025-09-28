package com.mttk.orche.agent.analysisSimple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;

@AgentTemplateFlag(key = "_analysis-simple", name = "多项式回归", description = """
                对输入CSV文件按照自变量,因变量进行多项式回归分析,结果增加一列保存到输出文件里.
                """, callDefineClass = AnalysisSimpleCallDfine.class, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "degree", label = "多项式回归的阶数", description = "多项式回归的阶数.", mandatory = true, size = 1, defaultVal = "3", props = {
                "dataType:number" })

public class AnalisysSimpleAgent extends AbstractAgent {
        @Override
        protected String executeInternal(AgentParam para, String transactionId) throws Exception {
                AgentContext context = para.getContext();
                AdapterConfig config = para.getConfig();
                AdapterConfig request = para.getRequest();

                //
                String inputFile = request.getString("inputFile");
                // load input file
                byte[] inputFileData = AgentUtil.getAgentFileService(context).downloadData(context, inputFile);

                //
                try (InputStream input = new ByteArrayInputStream(inputFileData);
                                ByteArrayOutputStream output = new ByteArrayOutputStream(
                                                (int) (inputFileData.length * 1.1))) {

                        List<String> independentVars = Arrays
                                        .asList(request.get("independentVars").toString().split(","));
                        String dependentVar = request.get("dependentVar").toString();

                        PolynomialRegression.Config config1 = new PolynomialRegression.Config();
                        config1.setPolynomialDegree(config.getInteger("degree", 3));

                        PolynomialRegression.Result result = PolynomialRegression.analyze(
                                        input, output, independentVars, dependentVar, config1);

                        // 生成目标文件
                        String outputFile = request.getString("outputFile");

                        // FileToolUtil.saveFile(context, output.toString("utf-8"), fileName);
                        // 返回结果
                        StringBuilder sb = new StringBuilder(2048);
                        sb.append("## 回归分析结果\n");
                        sb.append("输入数据文件名: " + inputFile + "\n");
                        sb.append("输出数据文件名: " + outputFile + "\n");
                        sb.append("LaTeX公式: " + result.getFormulaLatex() + "\n");
                        sb.append("R²: " + result.getRSquared() + "\n");
                        sb.append("调整R²: " + result.getAdjustedRSquared() + "\n");
                        sb.append("拟合值列名: " + result.getFittedColumnName() + "\n");
                        //
                        AgentUtil.getAgentFileService(context).upload(context, outputFile, sb.toString(),
                                        output.toByteArray());
                        //
                        return sb.toString();
                }
        }

}
