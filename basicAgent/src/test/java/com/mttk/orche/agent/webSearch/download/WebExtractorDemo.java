package com.mttk.orche.agent.webSearch.download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * WebContentExtractor演示类
 * 展示如何使用WebContentExtractor和JSoup提取网页内容
 */
public class WebExtractorDemo {

    public static void main(String[] args) throws Exception {
        // 测试URL - 2024年STEP时间安排文章
        // String testUrl = "https://liuxue.xdf.cn/blog/yuyongxiu/blog/4993366.shtml";
        String testUrl = "https://ueie.com/zh-hans/%E5%89%91%E6%A1%A5step%E8%80%83%E8%AF%95%E6%B7%B1%E5%BA%A6%E5%89%96%E6%9E%90/";

        System.out.println("=== WebContentExtractor + JSoup 演示 ===");
        System.out.println("测试URL: " + testUrl);
        System.out.println();

        // 步骤1: 使用WebContentExtractor下载网页内容
        System.out.println("步骤1: 下载网页内容...");
        String htmlContent = WebContentExtractor.extractWebText(testUrl);
        System.out.println("✓ 下载成功，HTML内容长度: " + htmlContent.length() + " 字符");
        System.out.println();

        // 步骤2: 使用JSoup解析HTML
        System.out.println("步骤2: 使用JSoup解析HTML...");
        Document doc = Jsoup.parse(htmlContent);
        System.out.println("✓ 解析成功");
        System.out.println();
        parseContent(doc);
        // System.out.println(parseContent(doc));

        // // 步骤3: 提取网页基本信息
        // System.out.println("步骤3: 提取网页信息...");

        // // 提取标题
        // String title = doc.title();
        // System.out.println("网页标题: " + title);

        // // 提取正文内容
        // String bodyText = doc.body().text();
        // System.out.println("正文内容长度: " + bodyText.length() + " 字符");

        // // 提取所有标题元素
        // int headingCount = doc.select("h1, h2, h3, h4, h5, h6").size();
        // System.out.println("标题元素数量: " + headingCount);

        // // 提取链接和图片
        // int linkCount = doc.select("a[href]").size();
        // int imageCount = doc.select("img[src]").size();
        // System.out.println("链接数量: " + linkCount);
        // System.out.println("图片数量: " + imageCount);
        // System.out.println();

        // // 步骤4: 显示内容预览
        // System.out.println("步骤4: 内容预览...");
        // System.out.println("----------------------------------------");
        // if (bodyText.length() > 500) {
        // System.out.println(bodyText.substring(0, 500) + "...");
        // } else {
        // System.out.println(bodyText);
        // }
        // System.out.println("----------------------------------------");
        // System.out.println();

        // // 步骤5: 提取特定内容（如文章标题）
        // System.out.println("步骤5: 提取特定内容...");
        // String mainTitle = doc.select("h1").text();
        // if (!mainTitle.isEmpty()) {
        // System.out.println("主标题: " + mainTitle);
        // }

        // // 提取文章内容区域
        // String articleContent = doc.select("article, .content,
        // .post-content").text();
        // if (!articleContent.isEmpty()) {
        // System.out.println("文章内容长度: " + articleContent.length() + " 字符");
        // }

        // System.out.println();
        // System.out.println("=== 演示完成 ===");
        // System.out.println("✓ WebContentExtractor成功下载网页内容");
        // System.out.println("✓ JSoup成功解析HTML并提取文本");
        // System.out.println("✓ 可以进一步处理提取的文本内容");

    }

    private static String parseContent(Document doc) {
        // 1. 直接移除已知垃圾标签
        System.out.println(
                "移除已知垃圾标签:"
                        + doc.select("script,style,noscript,iframe,footer,aside,nav,.ad,.header,.sidebar")
                                .size());

        doc.select("script,style,noscript,iframe,footer,aside,nav,.ad,.header,.sidebar").remove();

        // 显示文档层级结构
        // System.out.println("=== 文档层级结构 ===");
        // displayDocumentStructure(doc, 0);

        // 2. 优先尝试提取语义化标签
        // Element article =
        // doc.selectFirst("article,main,.article,.content,.post,.main");
        Element article = doc.selectFirst("article,main,.article,.content,.post,.main,.content");
        if (article != null) {
            System.out.println("提取到语义化标签:" + article.classNames());
            return article.text(); // 或保留结构化内容 article.html()
        }

        // 3. 备用方案：基于内容密度的正文提取
        return extractMainContent(doc);
    }

    private static String extractMainContent(Document doc) {
        // 启发式规则1：移除低文本密度元素
        doc.body().select("*").forEach(el -> {
            if (el.text().length() < 50 && el.children().size() > 2) {
                el.remove(); // 移除短文本多子元素的装饰性标签
            }
        });

        // 启发式规则2：选择文本最密集的连续区域
        int maxTextLen = 0;
        Element best = null;
        for (Element el : doc.select("p,div")) {
            int textLen = el.text().trim().length();
            int linkLen = el.select("a").text().length();

            // 排除链接占比高的元素（导航/广告）
            if (textLen > 200 && linkLen * 1.0 / textLen < 0.3) {
                if (textLen > maxTextLen) {
                    maxTextLen = textLen;
                    best = el;
                }
            }
        }

        return (best != null) ? best.text() : doc.body().text();
    }

    /**
     * 递归显示文档的层级结构
     * 
     * @param element 当前元素
     * @param level   当前层级深度
     */
    private static void displayDocumentStructure(Element element, int level) {
        // 限制显示深度，避免输出过多内容
        if (level > 5) {
            return;
        }

        // 生成缩进字符串
        String indent = "  ".repeat(level);

        // 显示当前元素信息
        String tagName = element.tagName();
        String className = element.className();
        String id = element.id();
        String text = element.ownText().trim();

        // 构建显示信息
        StringBuilder info = new StringBuilder();
        info.append(indent).append("<").append(tagName);

        if (!id.isEmpty()) {
            info.append(" id=\"").append(id).append("\"");
        }

        if (!className.isEmpty()) {
            info.append(" class=\"").append(className).append("\"");
        }

        info.append(">");

        // 如果有文本内容，显示前50个字符
        if (!text.isEmpty() && text.length() > 0) {
            String preview = text.length() > 50 ? text.substring(0, 50) + "..." : text;
            info.append(" \"").append(preview).append("\"");
        }

        // 显示子元素数量
        int childCount = element.children().size();
        if (childCount > 0) {
            info.append(" [").append(childCount).append(" 个子元素]");
        }

        System.out.println(info.toString());

        // 递归显示子元素（限制数量避免输出过多）
        int maxChildren = Math.min(childCount, 10); // 最多显示10个子元素
        for (int i = 0; i < maxChildren; i++) {
            displayDocumentStructure(element.children().get(i), level + 1);
        }

        // 如果子元素超过10个，显示省略信息
        if (childCount > 10) {
            System.out.println(indent + "  ... 还有 " + (childCount - 10) + " 个子元素");
        }
    }

}
