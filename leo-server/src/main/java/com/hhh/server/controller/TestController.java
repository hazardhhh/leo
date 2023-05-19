package com.hhh.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@RestController
public class TestController {

    @GetMapping("test")
    public String test() {
        return "test";
    }

}
