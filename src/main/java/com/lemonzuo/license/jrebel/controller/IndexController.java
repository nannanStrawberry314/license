package com.lemonzuo.license.jrebel.controller;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author LemonZuo
 * @create 2022-12-09 23:36
 */
@Slf4j
@RestController
public class IndexController {
    private static final String HTTP = "http";
    private static final String HTTP_PORT = "80";
    private static final String HTTPS = "https";
    private static final String HTTPS_PORT = "443";
    private static final String END_STR = "/";

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/")
    public void index(@RequestHeader(required = false, defaultValue = "") String scheme,
                      @RequestHeader(required = false, defaultValue = "") String host,
                      @RequestHeader(required = false, defaultValue = "") String port,
                      @RequestHeader(required = false, defaultValue = "") String requestUri,
                      HttpServletRequest request, HttpServletResponse response) {

        scheme = StrUtil.emptyToDefault(scheme, HTTP);
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(":"));
        }
        host = StrUtil.emptyToDefault(host, "127.0.0.1");
        port = StrUtil.emptyToDefault(port, serverPort);

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        // 拼接服务器地址
        if (StrUtil.isNotEmpty(requestUri) && requestUri.endsWith(END_STR)) {
            requestUri = requestUri.substring(0, requestUri.length() - 1);
        }

        String licenseUrl;
        if (HTTP.equals(scheme) && HTTP_PORT.equals(port)) {
            licenseUrl = String.format("%s://%s%s", scheme, host, requestUri);
        } else if (HTTPS.equals(scheme) && HTTPS_PORT.equals(port)) {
            licenseUrl = String.format("%s://%s%s", scheme, host, requestUri);
        } else {
            licenseUrl = String.format("%s://%s:%s%s", scheme, host, port, requestUri);
        }

        if (StrUtil.isEmpty(licenseUrl)) {
            licenseUrl = request.getRequestURL().substring(0, request.getRequestURL().length()-1);
        }

        StringBuilder html = new StringBuilder("<h3>使用说明（Instructions for use）</h3>");

        html.append("<hr/>");

        html.append("<h1>Hello,This is a Jrebel & JetBrains License Server!</h1>");
        html.append("<p>License Server started at ").append(licenseUrl);
        html.append("<p>JetBrains Activation address was: <span style='color:red'>").append(licenseUrl);
        html.append("<p>JRebel 7.1 and earlier version Activation address was: <span style='color:red'>")
                .append(licenseUrl).append("/{tokenname}")
                .append("</span>, with any email.");
        html.append("<p>JRebel 2018.1 and later version Activation address was: ")
                .append(licenseUrl).append("/{guid}")
                .append("(eg:<span style='color:red'>")
                .append(licenseUrl).append("/").append(UUID.randomUUID())
                .append("</span>), with any email.");

        html.append("<hr/>");

        html.append("<h1>Hello，此地址是 Jrebel & JetBrains License Server!</h1>");
        html.append("<p>JetBrains许可服务器激活地址 ").append(licenseUrl);
        html.append("<p>JetBrains激活地址是: <span style='color:red'>").append(licenseUrl);
        html.append("<p>JRebel 7.1 及旧版本激活地址: <span style='color:red'>")
                .append(licenseUrl).append("/{tokenname}")
                .append("</span>, 以及任意邮箱地址。");
        html.append("<p>JRebel 2018.1+ 版本激活地址: ")
                .append(licenseUrl).append("/{guid}")
                .append("(例如：<span style='color:red'>")
                .append(licenseUrl).append("/").append(UUID.randomUUID())
                .append("</span>), 以及任意邮箱地址。");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(html.toString());
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
