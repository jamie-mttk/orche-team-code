package com.mttk.orche.agent.fileConvert.support.pdf2md;

/**
 * 文本段落类，存储段落文本和位置信息
 */
public class TextParagraph {
    private final String text;
    private final float x;
    private final float y;

    public TextParagraph(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
