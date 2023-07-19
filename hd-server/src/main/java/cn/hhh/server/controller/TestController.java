package cn.hhh.server.controller;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.mapper.TestMapper;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.entity.vo.ths.TestRes;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "测试模块")
public class TestController {

    private static final HdLog log = HdLog.getInstance();

    @Autowired
    private TestMapper testMapper;

    @GetMapping("test")
    public BasicServiceModel<Object> test() {
        List<TestRes> testData = testMapper.getTestData();
        log.info("TestController | test = {}", testData);
        return BasicServiceModel.ok(testData);
    }

}
