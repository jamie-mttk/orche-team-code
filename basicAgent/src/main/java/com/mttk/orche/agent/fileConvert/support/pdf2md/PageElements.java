package com.mttk.orche.agent.fileConvert.support.pdf2md;

import technology.tabula.Table;

import java.util.List;

/**
 * 页面元素类，存储单个页面上的所有元素
 */
public class PageElements {
    private final List<TextParagraph> paragraphs;
    private final List<Table> tables;
    private final List<String> images;

    public PageElements(List<TextParagraph> paragraphs, List<Table> tables, List<String> images) {
        this.paragraphs = paragraphs;
        this.tables = tables;
        this.images = images;
    }

    public List<TextParagraph> getParagraphs() {
        return paragraphs;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<String> getImages() {
        return images;
    }
}
