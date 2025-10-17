package com.mttk.orche.agent.test;

import org.apache.commons.compress.archivers.zip.ZipFile;

import com.mttk.orche.agent.webSearch.download.TextSanitizer;
import com.mttk.orche.agent.webSearch.download.WebContentExtractor;

public class Test1 {
    public static void main(String[] args) throws Exception {
        // https://dc.pconline.com.cn/829/8294524_all.html

        org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument a;
        String content = WebContentExtractor.extractWebText(
                "https://www.amazon.com/-/zh_TW/Canon-Mark-DSLR-%E5%9C%8B%E9%9A%9B%E5%9E%8B%E8%99%9F-1483C002/dp/B0BXBD1YM3");
        System.out.println(content);
        // content = TextSanitizer.sanitize(content);

    }
}
