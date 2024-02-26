package com.lemonzuo.license.jrebel.config;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author LemonZuo
 * @create 2022-12-10 19:29
 */
@Configuration
public class MessageConverterConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonConfig config = new FastJsonConfig();
        config.setDateFormat(DatePattern.NORM_DATETIME_PATTERN);
        config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue);
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(0, converter);
    }
}
