package cn.hhh.server.controller;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.page.BasicPageRes;
import cn.hhh.server.entity.vo.ths.*;
import cn.hhh.server.service.ThsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 同花顺接口
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Api(tags = "主要模块")
@RestController
@RequestMapping("/ths")
public class ThsController {

    @Autowired
    private ThsService thsService;

    @PostMapping("/updateThsScoreDividend12Data")
    @ApiOperation(value = "更新股息率近12个月")
    public BasicServiceModel<Object> updateThsScoreDividend12Data() {
        return thsService.updateThsScoreDividend12Data();
    }

    @PostMapping("/updateThsScoreDividendData")
    @ApiOperation(value = "更新股息率2021,2020,过去3年平均值")
    public BasicServiceModel<Object> updateThsScoreDividendData() {
        return thsService.updateThsScoreDividendData();
    }

    @PostMapping("/updateThsScorePBData")
    @ApiOperation(value = "更新PB最新")
    public BasicServiceModel<Object> updateThsScorePBData() {
        return thsService.updateThsScorePBData();
    }

    @PostMapping("/updateThsScoreTurnoverRateByWeekData")
    @ApiOperation(value = "更新周换手率")
    public BasicServiceModel<Object> updateThsScoreTurnoverRateByWeekData() {
        return thsService.updateThsScoreTurnoverRateByWeekData();
    }

    @PostMapping("/updateThsScoreMarketValueData")
    @ApiOperation(value = "更新自由流通市值,总市值")
    public BasicServiceModel<Object> updateThsScoreMarketValueData() {
        return thsService.updateThsScoreMarketValueData();
    }

    @PostMapping("/updateThsScoreROEByQuarterData")
    @ApiOperation(value = "更新ROE最新季度")
    public BasicServiceModel<Object> updateThsScoreROEByQuarterData() {
        return thsService.updateThsScoreROEByQuarterData();
    }

    @PostMapping("/updateThsScoreROEData")
    @ApiOperation(value = "更新ROE2022,2021,2020,过去3年平均值")
    public BasicServiceModel<Object> updateThsScoreROEData() {
        return thsService.updateThsScoreROEData();
    }

    @PostMapping("/updateThsScoreRevenueAndNetProfitData")
    @ApiOperation(value = "更新营收增速,净利增速最新季度")
    public BasicServiceModel<Object> updateThsScoreRevenueAndNetProfitData() {
        return thsService.updateThsScoreRevenueAndNetProfitData();
    }

    @PostMapping("/rankScore")
    @ApiOperation(value = "计算排名百分比及打分")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<BasicPageRes> rankScore(@RequestBody GetRankScoreVo getRankScoreVo, HttpServletResponse response) {
        return thsService.rankScore(getRankScoreVo, response);
    }

    @PostMapping("/thsTest")
    @ApiOperation(value = "测试接口")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<Object> thsTest() {
        return thsService.thsTest();
    }

//    @PostMapping("/insertThsMemoir")
//    @ApiOperation(value = "新增同花顺预测评级,机构预测明细表")
//    public BasicServiceModel<Object> insertThsMemoir() {
//        return thsScoreService.insertThsMemoir(false);
//    }

    @PostMapping("/getSellerStudieData")
    @ApiOperation(value = "获取卖方研报信息")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<BasicPageRes> getSellerStudieData(@RequestBody GetSellerStudieVo getSellerStudieVo, HttpServletResponse response) {
        return thsService.getSellerStudieData(getSellerStudieVo, response);
    }

    @PostMapping("/getSellerStudieDataDetails")
    @ApiOperation(value = "获取卖方研报信息详情")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<Object> getSellerStudieDataDetails(@RequestBody GetSellerStudieDetailsVo getSellerStudieDetailsVo) {
        return thsService.getSellerStudieDataDetails(getSellerStudieDetailsVo);
    }

    @PostMapping("/getSellerStudieDataDetailsByGxjg")
    @ApiOperation(value = "获取卖方研报信息,国信金工被券商金股覆盖详情")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<Object> getSellerStudieDataDetailsByGxjg(@RequestBody GetSellerStudieDetailsByGxjgVo getSellerStudieDetailsByGxjgVo) {
        return thsService.getSellerStudieDataDetailsByGxjg(getSellerStudieDetailsByGxjgVo);
    }

    @PostMapping("/updateDictSwData")
    @ApiOperation(value = "更新申万所属行业字典")
    public BasicServiceModel<Object> updateDictSwData() {
        return thsService.updateDictSwData();
    }

//    @PostMapping("/insertThsBehavior")
//    @ApiOperation(value = "新增同花顺用户行为统计表")
//    public BasicServiceModel<Object> insertThsBehavior() {
//        return thsScoreService.insertThsBehavior();
//    }

    @PostMapping("/getThsBehaviorData")
    @ApiOperation(value = "获取用户行为统计信息")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<BasicPageRes> getThsBehaviorData(@RequestBody GetThsBehaviorVo getThsBehaviorVo, HttpServletResponse response) {
        return thsService.getThsBehaviorData(getThsBehaviorVo, response);
    }

//    @PostMapping("/insertThsHot")
//    @ApiOperation(value = "新增同花顺个股人气表")
//    public BasicServiceModel<Object> insertThsHot() {
//        return thsScoreService.insertThsHot();
//    }

    @PostMapping("/getThsBehaviorAndHotRankData")
    @ApiOperation(value = "获取用户行为统计和个股人气信息排名")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<BasicPageRes> getThsBehaviorAndHotRankData(@RequestBody GetThsBehaviorAndHotRankVo getThsBehaviorAndHotRankVo, HttpServletResponse response) {
        return thsService.getThsBehaviorAndHotRankData(getThsBehaviorAndHotRankVo, response);
    }

}
