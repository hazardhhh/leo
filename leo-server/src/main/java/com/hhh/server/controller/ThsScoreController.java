package com.hhh.server.controller;

import com.hhh.server.pojo.RespRes;
import com.hhh.server.service.ThsScoreService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 同花顺接口
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Api(tags = "ThsScoreController")
@RestController
@Slf4j
public class ThsScoreController {

    @Autowired
    private ThsScoreService thsScoreService;

    @GetMapping("updateThsScoreData")
    public RespRes updateThsScoreData() {

        return thsScoreService.updateThsScore();
    }

}
