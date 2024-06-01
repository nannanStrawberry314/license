package com.lemonzuo.license.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LemonZuo
 * @create 2024-06-01 22:38
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("status", true);
    }
}
