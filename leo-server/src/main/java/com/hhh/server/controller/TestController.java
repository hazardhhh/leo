package com.hhh.server.controller;

import com.hhh.server.logger.LeoLog;
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

    private static final LeoLog log = LeoLog.getInstance();

    @GetMapping("test")
    public String test() {
        String test = "test";
        log.info("TestController | test = {}", test);
        return "test";
    }

}
