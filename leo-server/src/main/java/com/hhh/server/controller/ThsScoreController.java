package com.hhh.server.controller;

import com.hhh.server.pojo.RespRes;
import com.hhh.server.service.ThsScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("updateThsScoreDividend12Data")
    @ApiOperation(value = "更新股息率近12个月")
    public RespRes updateThsScoreDividend12Data() {
        return thsScoreService.updateThsScoreDividend12Data();
    }

    @PostMapping("updateThsScoreDividendData")
    @ApiOperation(value = "更新股息率2021,2020,过去3年平均值")
    public RespRes updateThsScoreDividendData() {
        return thsScoreService.updateThsScoreDividendData();
    }

}
