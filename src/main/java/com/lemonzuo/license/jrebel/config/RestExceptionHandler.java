package com.lemonzuo.license.jrebel.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author LemonZuo
 * @create 2022-12-10 18:59
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    private JSONObject error;
    private void init() {
        if (ObjectUtil.isNull(error)) {
            error = new JSONObject();
            error.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.put("msg", "系统异常");
        }
    }
    @ExceptionHandler(value = Exception.class)
    public JSONObject handleException(Exception exception) {
        init();
        log.error("", exception);
        return error;
    }
}
