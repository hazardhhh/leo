package cn.hhh.server.controller;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.logger.HdLog;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@RestController
@Api(tags = "测试模块")
public class TestController {

    private static final HdLog log = HdLog.getInstance();

    @GetMapping("test")
    public BasicServiceModel<Object> test() {
        log.info("TestController | test = {}", "testData");
        return BasicServiceModel.ok("testData");
    }

}
