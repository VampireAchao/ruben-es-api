package com.ruben.utils;

import com.ruben.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: HtmlParseUtil
 * @Date: 2020/4/15 20:53
 * @Description:
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("中文测试").forEach(System.out::println);
    }

    public List<Content> parseJD(String keywords) throws IOException {
        keywords = new String(keywords.getBytes(), "utf-8");
        ArrayList<Content> goodsList = new ArrayList<>();
        //获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=";
        //解析网页 jsoup返回的就是浏览器document对象
        Document document = Jsoup.parse(new URL(url + keywords + "&enc=utf-8&wq=" + keywords), 60000);
        System.out.println(url + keywords + "&enc=utf-8&wq=" + keywords);
        //所有在js中能用的方法，这里都能用
        Element element = document.getElementById("J_goodsList");
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        //获取元素中的内容,这里的el就是每一个li标签
        for (Element el : elements) {
            //关于图片特别多的网站，所有图片都是延迟加载的
            //source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);
        }
        return goodsList;
    }
}
