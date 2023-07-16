package com.hhh.server.controller;

import com.hhh.server.logger.LeoLog;
import com.hhh.server.mapper.TestMapper;
import com.hhh.server.pojo.TestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@RestController
@CrossOrigin
public class TestController {

    private static final LeoLog log = LeoLog.getInstance();

    @Autowired
    private TestMapper testMapper;

    @GetMapping("test")
    public String test() {
        List<TestRes> testData = testMapper.getTestData();
        log.info("TestController | test = {}", testData);
        return "test";
    }

}
