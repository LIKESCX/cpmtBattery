package com.cpit.cpmt.biz.utils.exchange;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.util.Map;

/***
 * http请求工具类
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    /**
     * http get请求
     * 获取返回值
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        HttpRequest httpRequest = HttpRequest.get(url).acceptEncoding("gzip");
        HttpResponse response = httpRequest.send().unzip();
        return response.bodyText();
    }

    /**
     * http post请求
     *
     * @param url
     * @param formMap
     * @return
     */
    public static String post(String url, Map<String, Object> formMap) {
        HttpRequest httpRequest = HttpRequest.post(url).acceptEncoding("gzip");
        httpRequest.form(formMap);
        HttpResponse response = httpRequest.send().unzip();
        return response.bodyText();
    }

    /***
     * 发送body字符串
     * @param url
     * @param bodyText
     * @return
     */
    public static String postBody(String url, Map<String, String> headerMap, String bodyText) {
        HttpRequest httpRequest = HttpRequest.post(url).acceptEncoding("gzip").body(bodyText);
//        HttpRequest httpRequest = HttpRequest.post(url).acceptEncoding("gzip").bodyText(bodyText,"application/json");
        if (headerMap != null && !headerMap.isEmpty()) {
            httpRequest.header(headerMap);
            if (!headerMap.containsKey("Content-Type")) {
                httpRequest.header("Content-Type", "text/plain; charset=UTF-8;");
            }
        } else {
            httpRequest.header("Content-Type", "text/plain; charset=UTF-8;");
        }
//        httpRequest.header("Content-Type","text/plain;application/json;application/x-www-form-urlencoded;charset=UTF-8;");
        HttpResponse response = httpRequest.send().unzip();
        return response.bodyText();
    }

    public static String postBody(String url, String bodyText) {
        return postBody(url, null, bodyText);
    }
}
