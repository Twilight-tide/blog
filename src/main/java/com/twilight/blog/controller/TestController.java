package com.twilight.blog.controller;

import com.twilight.blog.annotation.PublicApi;
import com.twilight.blog.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PublicApi
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("pong");
    }

    @GetMapping("/hello")
    public R<String> hello() {
        return R.ok("你好，我改好了");
    }
}