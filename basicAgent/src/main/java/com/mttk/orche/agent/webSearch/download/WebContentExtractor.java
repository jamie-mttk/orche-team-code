package com.mttk.orche.agent.webSearch.download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mttk.orche.util.StringUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebContentExtractor {

    /**
     * 创建信任所有证书的SSL上下文
     * 注意：此方法仅用于开发环境，生产环境应使用正确的证书验证
     */
    private static SSLContext createTrustAllSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // 信任所有客户端证书
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // 信任所有服务器证书
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }

    /**
     * 获取并解析网页文本内容
     *
     * @param url 目标网页URL
     * @return 解析后的纯文本内容
     * @throws Exception 包含详细错误信息的异常
     */
    public static String extractWebText(String url) throws Exception {
        // 验证URL格式
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("URL不能为空");
        }
        if (!url.matches("^(https?|ftp)://.*$")) {
            throw new IllegalArgumentException("无效的URL格式: " + url);
        }

        try {
            // 创建HTTP客户端，使用信任所有证书的SSL上下文
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .sslContext(createTrustAllSSLContext())
                    .build();

            // 构建请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "WebSearchAgent/1.0")
                    .timeout(java.time.Duration.ofSeconds(15))
                    .GET()
                    .build();

            // 发送请求，获取字节数组以便正确处理编码
            CompletableFuture<HttpResponse<byte[]>> future = client.sendAsync(request, BodyHandlers.ofByteArray());
            HttpResponse<byte[]> response = future.get(15, TimeUnit.SECONDS);
            // 检查响应状态
            int statusCode = response.statusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HTTP错误响应: " + statusCode +
                        " - " + response.uri() +
                        "\n响应头: " + response.headers());
            }

            if (!isAllowedContentType(response)) {
                // 不支持的类型,如PDF暂时忽略
                return "";
            }

            // 检测编码
            String charset = detectCharset(response);
            // charset = "big5";
            // System.out.println("编码: " + charset);
            // 使用正确的编码解码内容
            String htmlContent = decodeContent(response.body(), charset);

            // 使用JSoup提取纯文本
            return parseContent(htmlContent);

        } catch (Exception e) {
            // 包装异常并添加上下文信息
            throw new Exception("处理URL时出错: " + url + " - " + e.getMessage(), e);
        }
    }

    private static String parseContent(String html) {
        Document doc = Jsoup.parse(html);
        // 1. 直接移除已知垃圾标签
        doc.select("script,style,noscript,iframe,footer,aside,nav,.ad,.header,.sidebar").remove();

        // 2. 优先尝试提取语义化标签
        Element article = doc.selectFirst("article,main,.article,.content,.post,.main,.content");
        if (article != null) {
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

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "text/html",
            "text/plain",
            "text/xml",
            "application/json",
            "application/xml",
            "application/octet-stream");

    /**
     * 从响应头和HTML内容中检测字符编码
     * 
     * @param response HTTP响应
     * @return 检测到的字符集名称，默认为UTF-8
     */
    private static String detectCharset(HttpResponse<byte[]> response) {
        // 1. 优先从Content-Type响应头获取charset
        Optional<String> contentTypeHeader = response.headers().firstValue("Content-Type");
        if (contentTypeHeader.isPresent()) {
            String contentType = contentTypeHeader.get();
            Pattern charsetPattern = Pattern.compile("charset\\s*=\\s*([^;\\s]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = charsetPattern.matcher(contentType);
            if (matcher.find()) {
                String charset = matcher.group(1).replace("\"", "").replace("'", "").trim();
                if (Charset.isSupported(charset)) {
                    return charset;
                }
            }
        }

        // 2. 从HTML的meta标签中提取charset
        // 只扫描前2048字节以提高性能
        byte[] bodyBytes = response.body();
        int scanLength = Math.min(2048, bodyBytes.length);
        String preview = new String(bodyBytes, 0, scanLength, StandardCharsets.ISO_8859_1);

        // 匹配 <meta charset="xxx">
        Pattern metaCharsetPattern = Pattern.compile(
                "<meta\\s+charset\\s*=\\s*[\"']?([^\"'\\s>]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = metaCharsetPattern.matcher(preview);
        if (matcher.find()) {
            String charset = matcher.group(1).trim();
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }

        // 匹配 <meta http-equiv="Content-Type" content="text/html; charset=xxx">
        Pattern metaHttpEquivPattern = Pattern.compile(
                "<meta\\s+http-equiv\\s*=\\s*[\"']?Content-Type[\"']?\\s+content\\s*=\\s*[\"']?[^\"'>]*charset\\s*=\\s*([^\"';\\s>]+)",
                Pattern.CASE_INSENSITIVE);
        matcher = metaHttpEquivPattern.matcher(preview);
        if (matcher.find()) {
            String charset = matcher.group(1).trim();
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }

        // 3. 默认使用UTF-8
        return "UTF-8";
    }

    /**
     * 使用指定的字符集解码字节数组
     * 
     * @param bytes       字节数组
     * @param charsetName 字符集名称
     * @return 解码后的字符串
     */
    private static String decodeContent(byte[] bytes, String charsetName) {
        try {
            Charset charset = Charset.forName(charsetName);
            return new String(bytes, charset);
        } catch (Exception e) {
            // 如果指定的编码不支持，回退到UTF-8
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private static boolean isAllowedContentType(HttpResponse<byte[]> response) {
        // 从响应中获取 Content-Type 头部
        Optional<String> contentTypeHeader = response.headers().firstValue("Content-Type");

        if (!contentTypeHeader.isPresent()) {
            return false; // 没有 Content-Type 头部
        }

        // 提取主 MIME 类型（移除参数如 charset）
        String fullType = contentTypeHeader.get().split(";")[0].trim().toLowerCase();

        // 检查是否在允许的类型列表中
        return ALLOWED_TYPES.contains(fullType);
    }

    public static void main(String[] args) {

        String url = "https://www.caac.gov.cn/XXGK/XXGK/GFXWJ/201511/P020151103346536425913.pdf";
        System.out.println("正在处理: " + url);

        try {
            String content = extractWebText(url);
            System.out.println("\n提取的文本内容:");
            System.out.println(content);
        } catch (Exception e) {
            System.err.println("\n处理失败:");
            e.printStackTrace();
            System.exit(2);
        }
    }
}
