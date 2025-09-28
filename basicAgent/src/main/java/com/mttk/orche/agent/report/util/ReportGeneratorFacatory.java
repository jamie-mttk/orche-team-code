package com.mttk.orche.agent.report.util;

import java.util.Map;

import com.mttk.orche.addon.AdapterConfig;

public class ReportGeneratorFacatory {
    public static ReportGenerator build(AdapterConfig request) {
        String fileType = request.getString("fileType", "markdown");
        if ("html".equalsIgnoreCase(fileType)) {
            return new HtmlReportGenerator();
        } else if ("ppt".equalsIgnoreCase(fileType)) {
            return new PptReportGenerator();
        } else if ("markdown".equalsIgnoreCase(fileType)) {
            return new MarkdownReportGenerator();
        } else {
            throw new RuntimeException("Unsupported  report type :" + fileType);
        }
    }
}
