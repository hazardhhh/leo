package com.hhh.server.controller;

import com.hhh.server.pojo.*;
import com.hhh.server.service.ThsScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 同花顺接口
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Api(tags = "ThsScoreController")
@RestController
@CrossOrigin
public class ThsScoreController {

    @Autowired
    private ThsScoreService thsScoreService;

    @PostMapping("/thsScore/updateThsScoreDividend12Data")
    @ApiOperation(value = "更新股息率近12个月")
    public RespRes updateThsScoreDividend12Data() {
        return thsScoreService.updateThsScoreDividend12Data();
    }

    @PostMapping("/thsScore/updateThsScoreDividendData")
    @ApiOperation(value = "更新股息率2021,2020,过去3年平均值")
    public RespRes updateThsScoreDividendData() {
        return thsScoreService.updateThsScoreDividendData();
    }

    @PostMapping("/thsScore/updateThsScorePBData")
    @ApiOperation(value = "更新PB最新")
    public RespRes updateThsScorePBData() {
        return thsScoreService.updateThsScorePBData();
    }

    @PostMapping("/thsScore/updateThsScoreTurnoverRateByWeekData")
    @ApiOperation(value = "更新周换手率")
    public RespRes updateThsScoreTurnoverRateByWeekData() {
        return thsScoreService.updateThsScoreTurnoverRateByWeekData();
    }

    @PostMapping("/thsScore/updateThsScoreMarketValueData")
    @ApiOperation(value = "更新自由流通市值,总市值")
    public RespRes updateThsScoreMarketValueData() {
        return thsScoreService.updateThsScoreMarketValueData();
    }

    @PostMapping("/thsScore/updateThsScoreROEByQuarterData")
    @ApiOperation(value = "更新ROE最新季度")
    public RespRes updateThsScoreROEByQuarterData() {
        return thsScoreService.updateThsScoreROEByQuarterData();
    }

    @PostMapping("/thsScore/updateThsScoreROEData")
    @ApiOperation(value = "更新ROE2022,2021,2020,过去3年平均值")
    public RespRes updateThsScoreROEData() {
        return thsScoreService.updateThsScoreROEData();
    }

    @PostMapping("/thsScore/updateThsScoreRevenueAndNetProfitData")
    @ApiOperation(value = "更新营收增速,净利增速最新季度")
    public RespRes updateThsScoreRevenueAndNetProfitData() {
        return thsScoreService.updateThsScoreRevenueAndNetProfitData();
    }

    @PostMapping("/thsScore/rankPCTScore")
    @ApiOperation(value = "计算排名百分比及打分")
    public BasicPageRes<RespRes> rankPCTScore(@RequestBody RankPCTScoreReq rankPCTScoreReq, HttpServletResponse response) {
        return thsScoreService.rankPCTScore(rankPCTScoreReq, response);
    }

    @PostMapping("/thsScore/thsTest")
    @ApiOperation(value = "测试接口")
    public RespRes thsTest() {
        return thsScoreService.thsTest();
    }

//    @PostMapping("/thsScore/insertThsMemoir")
//    @ApiOperation(value = "新增同花顺预测评级,机构预测明细表")
//    public RespRes insertThsMemoir() {
//        return thsScoreService.insertThsMemoir(false);
//    }

    @PostMapping("/thsScore/getSellerStudieData")
    @ApiOperation(value = "获取卖方研报信息")
    public BasicPageRes<RespRes> getSellerStudieData(@RequestBody SellerStudieReq sellerStudieReq, HttpServletResponse response) {
        return thsScoreService.getSellerStudieData(sellerStudieReq, response);
    }

    @PostMapping("/thsScore/getSellerStudieDataDetails")
    @ApiOperation(value = "获取卖方研报信息详情")
    public RespRes getSellerStudieDataDetails(@RequestBody SellerStudieDetailsReq sellerStudieDetailsReq) {
        return thsScoreService.getSellerStudieDataDetails(sellerStudieDetailsReq);
    }

    @PostMapping("/thsScore/getSellerStudieDataDetailsByGxjg")
    @ApiOperation(value = "获取卖方研报信息,国信金工被券商金股覆盖详情")
    public RespRes getSellerStudieDataDetailsByGxjg(@RequestBody SellerStudieDetailsByGxjgReq sellerStudieDetailsByGxjgReq) {
        return thsScoreService.getSellerStudieDataDetailsByGxjg(sellerStudieDetailsByGxjgReq);
    }

    @PostMapping("/thsScore/updateDictSwData")
    @ApiOperation(value = "更新申万所属行业字典")
    public RespRes updateDictSwData() {
        return thsScoreService.updateDictSwData();
    }

}
