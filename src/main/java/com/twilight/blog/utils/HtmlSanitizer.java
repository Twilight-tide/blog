package com.twilight.blog.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 富文本入库前的清洗：只保留安全的 HTML 标签与属性。
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    private static final Safelist SAFELIST = Safelist.relaxed()
            // 允许 Markdown 渲染需要的额外标签
            .addTags("figure", "figcaption", "hr", "del", "ins")
            // 图片属性
            .addAttributes("img", "alt", "title")
            // 代码块属性
            .addAttributes("pre", "class")
            .addAttributes("code", "class")
            // 表格属性
            .addAttributes("th", "align")
            .addAttributes("td", "align")
            // 链接属性：只允许 href、title
            .addAttributes("a", "href", "title")
            // ⚠️ 明确禁止 style 属性（防止 CSS 注入）
            .removeAttributes(":all", "style")
            // ⚠️ 明确禁止 target 属性（防止 tabnabbing）
            .removeAttributes(":all", "target")
            // 只允许这些协议
            .addProtocols("a", "href", "http", "https", "mailto")
            .addProtocols("img", "src", "http", "https");

    public static String clean(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        return Jsoup.clean(html, "", SAFELIST,
                new Document.OutputSettings().prettyPrint(false));
    }
}