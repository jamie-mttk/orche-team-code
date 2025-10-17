package com.mttk.orche.agent.fileConvert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.agent.fileConvert.support.ConvertOptions;
import com.mttk.orche.agent.fileConvert.support.FileConvertFacade;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import com.mttk.orche.agent.fileConvert.support.excel2csv.MergedCellHandlingMode;
import com.mttk.orche.util.StringUtil;

import org.apache.poi.ss.usermodel.Sheet;

@AgentTemplateFlag(key = "_file-convert-agent", name = "文件转换", description = "将PPT/PDF/Word转换为Markdown,将Excel转换为CSV.每次只能转换一个文件.", callDefineClass = FileConvertCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "pptIncludeNotes", label = "PPT包含备注", description = "PPT转Markdown时是否包含演讲者备注", mode = "checkbox", size = 1, defaultVal = "false")
@Control(key = "pdfIncludePageNumbers", label = "PDF包含页码", description = "PDF转Markdown时是否包含页码标题", mode = "checkbox", size = 1, defaultVal = "false")
@Control(key = "excelMergedCellMode", label = "Excel合并单元格", description = "Excel转CSV时合并单元格的处理模式", mode = "select", size = 1, defaultVal = "TOP_LEFT_ONLY", props = "options:TOP_LEFT_ONLY:仅左上角,HORIZONTAL_FILL:水平填充,VERTICAL_FILL:垂直填充,ALL_FILL:全部填充")
@Control(key = "excelSkipEmptyRows", label = "Excel跳过空行", description = "Excel转CSV时是否跳过空行", mode = "checkbox", size = 1, defaultVal = "false")
@Control(key = "errorFormulaMode", label = "Excel错误公式", description = "Excel错误公式的处理模式", mode = "select", size = 1, defaultVal = "NONE", props = "options:NONE:返回空字符串,RAW:返回原始公式")
@Control(key = "excelDateFormat", label = "Excel日期格式", description = "Excel转CSV时日期单元格的格式化模式", mandatory = true, size = 1, defaultVal = "yyyy/MM/dd HH:mm:ss")
public class FileConvertAgent extends AbstractAgent {

    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        AgentContext context = para.getContext();
        AdapterConfig config = para.getConfig();
        AdapterConfig request = para.getRequest();

        // 获取输入文件名
        String inputFileName = request.getString("inputFileName");
        if (StringUtil.isEmpty(inputFileName)) {
            throw new IllegalArgumentException("输入文件名不能为空");
        }

        // 获取输出文件名（可选）
        String outputFileName = request.getString("outputFileName");

        // 从文件服务获取输入文件数据
        byte[] fileData = AgentUtil.getAgentFileService(context).downloadData(context, inputFileName);
        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException("无法读取输入文件: " + inputFileName);
        }

        // 构建转换选项
        Map<String, Object> options = buildConvertOptions(config);

        // 确定输出文件名和扩展名
        String targetExtension = getTargetExtension(inputFileName);
        if (StringUtil.isEmpty(outputFileName)) {
            outputFileName = generateOutputFileName(inputFileName, targetExtension);
        } else if (!outputFileName.toLowerCase().endsWith("." + targetExtension)) {
            outputFileName = outputFileName + "." + targetExtension;
        }

        // 执行转换
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {

            // 根据文件类型决定是否需要处理多个输出（Excel可能有多个Sheet）
            if (isExcelFile(inputFileName)) {
                // Excel转换，可能产生多个CSV文件
                return convertExcel(context, inputStream, inputFileName, outputFileName, options);
            } else {
                // PPT/PDF/Word转换，只产生一个Markdown文件
                return convertDocument(context, inputStream, inputFileName, outputFileName, options);
            }
        }
    }

    /**
     * 转换文档文件（PPT/PDF/Word -> Markdown）
     * 
     * @return 转换结果描述信息
     */
    private String convertDocument(AgentContext context, InputStream inputStream, String inputFileName,
            String outputFileName, Map<String, Object> options) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        OutputCallback callback = new OutputCallback() {
            @Override
            public java.io.OutputStream getOutputStream(Object indicator) throws Exception {
                return outputStream;
            }
        };

        FileConvertFacade.convert(inputFileName, inputStream, callback, options);

        // 保存转换后的文件
        byte[] convertedData = outputStream.toByteArray();
        AgentUtil.getAgentFileService(context).upload(context, outputFileName,
                "从 " + inputFileName + " 转换而来", convertedData);

        return "文件转换成功: " + inputFileName + " -> " + outputFileName + " (大小: " + convertedData.length + " 字节)";
    }

    /**
     * 转换Excel文件（可能生成多个CSV文件）
     * 
     * @return 转换结果描述信息，包含所有生成的文件名
     */
    private String convertExcel(AgentContext context, InputStream inputStream, String inputFileName,
            String outputFileName, Map<String, Object> options) throws Exception {

        Map<String, ByteArrayOutputStream> sheetOutputs = new HashMap<>();

        OutputCallback callback = new OutputCallback() {
            @Override
            public java.io.OutputStream getOutputStream(Object indicator) throws Exception {
                if (indicator instanceof Sheet) {
                    Sheet sheet = (Sheet) indicator;
                    String sheetName = sheet.getSheetName();

                    // 为每个Sheet创建输出流
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    sheetOutputs.put(sheetName, outputStream);
                    return outputStream;
                }
                return null;
            }
        };

        FileConvertFacade.convert(inputFileName, inputStream, callback, options);

        // 保存所有生成的CSV文件并收集文件名
        StringBuilder result = new StringBuilder();
        result.append("文件转换成功: ").append(inputFileName).append(" -> ");

        if (sheetOutputs.isEmpty()) {
            result.append("未生成任何CSV文件（Excel文件可能为空）");
        } else if (sheetOutputs.size() == 1) {
            // 单个Sheet
            Map.Entry<String, ByteArrayOutputStream> entry = sheetOutputs.entrySet().iterator().next();
            String sheetName = entry.getKey();
            ByteArrayOutputStream outputStream = entry.getValue();
            byte[] csvData = outputStream.toByteArray();

            AgentUtil.getAgentFileService(context).upload(context, outputFileName,
                    "从 " + inputFileName + " (Sheet: " + sheetName + ") 转换而来", csvData);

            result.append(outputFileName).append(" (大小: ").append(csvData.length).append(" 字节)");
        } else {
            // 多个Sheet
            result.append(sheetOutputs.size()).append(" 个CSV文件：");
            int index = 0;
            for (Map.Entry<String, ByteArrayOutputStream> entry : sheetOutputs.entrySet()) {
                String sheetName = entry.getKey();
                ByteArrayOutputStream outputStream = entry.getValue();
                byte[] csvData = outputStream.toByteArray();

                String baseFileName = removeExtension(outputFileName);
                String csvFileName = baseFileName + "_" + sanitizeFileName(sheetName) + ".csv";

                AgentUtil.getAgentFileService(context).upload(context, csvFileName,
                        "从 " + inputFileName + " (Sheet: " + sheetName + ") 转换而来", csvData);

                if (index > 0) {
                    result.append(", ");
                }
                result.append(csvFileName);
                index++;
            }
        }

        return result.toString();
    }

    /**
     * 构建转换选项
     */
    private Map<String, Object> buildConvertOptions(AdapterConfig config) {
        Map<String, Object> options = new HashMap<>();

        // PPT选项：是否包含备注
        String pptIncludeNotes = config.getString("pptIncludeNotes", "false");
        options.put(ConvertOptions.PPT_INCLUDE_NOTES, Boolean.parseBoolean(pptIncludeNotes));

        // PDF选项：是否包含页码
        String pdfIncludePageNumbers = config.getString("pdfIncludePageNumbers", "false");
        options.put(ConvertOptions.PDF_INCLUDE_PAGE_NUMBERS, Boolean.parseBoolean(pdfIncludePageNumbers));

        // Excel选项：合并单元格处理模式
        String excelMergedCellMode = config.getString("excelMergedCellMode", "TOP_LEFT_ONLY");
        try {
            options.put(ConvertOptions.EXCEL_MERGED_CELL_MODE, MergedCellHandlingMode.valueOf(excelMergedCellMode));
        } catch (IllegalArgumentException e) {
            options.put(ConvertOptions.EXCEL_MERGED_CELL_MODE, MergedCellHandlingMode.TOP_LEFT_ONLY);
        }

        // Excel选项：是否跳过空行
        String excelSkipEmptyRows = config.getString("excelSkipEmptyRows", "false");
        options.put(ConvertOptions.EXCEL_SKIP_EMPTY_ROWS, Boolean.parseBoolean(excelSkipEmptyRows));

        // Excel选项：错误公式处理模式
        String errorFormulaMode = config.getString("errorFormulaMode", "NONE");
        options.put(ConvertOptions.ERROR_FORMULA_MODE, errorFormulaMode);

        // Excel选项：日期格式
        String excelDateFormat = config.getString("excelDateFormat", "yyyy/MM/dd HH:mm:ss");
        options.put(ConvertOptions.EXCEL_DATE_FORMAT, excelDateFormat);

        return options;
    }

    /**
     * 判断是否为Excel文件
     */
    private boolean isExcelFile(String fileName) {
        String extension = getFileExtension(fileName);
        return "xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * 移除文件扩展名
     */
    private String removeExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 根据输入文件类型确定目标文件扩展名
     */
    private String getTargetExtension(String inputFileName) {
        String extension = getFileExtension(inputFileName).toLowerCase();

        switch (extension) {
            case "xls":
            case "xlsx":
                return "csv";
            case "ppt":
            case "pptx":
            case "pdf":
            case "doc":
            case "docx":
                return "md";
            default:
                return "txt";
        }
    }

    /**
     * 生成输出文件名
     */
    private String generateOutputFileName(String inputFileName, String targetExtension) {
        String baseName = removeExtension(inputFileName);
        return baseName + "." + targetExtension;
    }

    /**
     * 清理文件名中的非法字符
     */
    private String sanitizeFileName(String fileName) {
        // 替换文件名中的非法字符为下划线
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
