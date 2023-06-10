package com.hhh.server.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺打分结果
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("同花顺打分结果")
@AllArgsConstructor
@NoArgsConstructor
public class ThsScoreRes {

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "申万一级")
    private String swFirst;

    @ApiModelProperty(value = "申万二级")
    private String swSecond;

    @ApiModelProperty(value = "企业属性")
    private String corpAttr;

    @ApiModelProperty(value = "股息率近12个月")
    private String dividend12;

    @ApiModelProperty(value = "股息率2022")
    private String dividend2022;

    @ApiModelProperty(value = "股息率2021")
    private String dividend2021;

    @ApiModelProperty(value = "股息率2020")
    private String dividend2020;

    @ApiModelProperty(value = "股息率过去3年平均值")
    private String dividendAverage3;

    @ApiModelProperty(value = "PB最新")
    private String PB;

    @ApiModelProperty(value = "周换手率")
    private String turnoverRateByWeek;

    @ApiModelProperty(value = "自由流通市值")
    private String freeFlowMarketValue;

    @ApiModelProperty(value = "总市值")
    private String allMarketValue;

    @ApiModelProperty(value = "ROE最新季度")
    private String ROEByQuarter;

    @ApiModelProperty(value = "ROE2022")
    private String ROE2022;

    @ApiModelProperty(value = "ROE2021")
    private String ROE2021;

    @ApiModelProperty(value = "ROE2020")
    private String ROE2020;

    @ApiModelProperty(value = "ROE过去3年平均值")
    private String ROEAverage3;

    @ApiModelProperty(value = "营收增速最新季度")
    private String revenue;

    @ApiModelProperty(value = "净利增速最新季度")
    private String netProfit;

    @ApiModelProperty(value = "股息率近12个月PCT")
    private String dividend12PCT;

    @ApiModelProperty(value = "股息率近12个月Score")
    private String dividend12Score;

    @ApiModelProperty(value = "股息率2022PCT")
    private String dividend2022PCT;

    @ApiModelProperty(value = "股息率2022Score")
    private String dividend2022Score;

    @ApiModelProperty(value = "股息率2021PCT")
    private String dividend2021PCT;

    @ApiModelProperty(value = "股息率2021Score")
    private String dividend2021Score;

    @ApiModelProperty(value = "股息率2020PCT")
    private String dividend2020PCT;

    @ApiModelProperty(value = "股息率2020Score")
    private String dividend2020Score;

    @ApiModelProperty(value = "股息率过去3年平均值PCT")
    private String dividendAverage3PCT;

    @ApiModelProperty(value = "股息率过去3年平均值Score")
    private String dividendAverage3Score;

    @ApiModelProperty(value = "PB最新PCT")
    private String PBPCT;

    @ApiModelProperty(value = "PB最新Score")
    private String PBScore;

    @ApiModelProperty(value = "周换手率PCT")
    private String turnoverRateByWeekPCT;

    @ApiModelProperty(value = "周换手率Score")
    private String turnoverRateByWeekScore;

    @ApiModelProperty(value = "自由流通市值PCT")
    private String freeFlowMarketValuePCT;

    @ApiModelProperty(value = "自由流通市值Score")
    private String freeFlowMarketValueScore;

    @ApiModelProperty(value = "总市值PCT")
    private String allMarketValuePCT;

    @ApiModelProperty(value = "总市值Score")
    private String allMarketValueScore;

    @ApiModelProperty(value = "ROE最新季度PCT")
    private String ROEByQuarterPCT;

    @ApiModelProperty(value = "ROE最新季度Score")
    private String ROEByQuarterScore;

    @ApiModelProperty(value = "ROE2022PCT")
    private String ROE2022PCT;

    @ApiModelProperty(value = "ROE2022Score")
    private String ROE2022Score;

    @ApiModelProperty(value = "ROE2021PCT")
    private String ROE2021PCT;

    @ApiModelProperty(value = "ROE2021Score")
    private String ROE2021Score;

    @ApiModelProperty(value = "ROE2020PCT")
    private String ROE2020PCT;

    @ApiModelProperty(value = "ROE2020Score")
    private String ROE2020Score;

    @ApiModelProperty(value = "ROE过去3年平均值PCT")
    private String ROEAverage3PCT;

    @ApiModelProperty(value = "ROE过去3年平均值Score")
    private String ROEAverage3Score;

    @ApiModelProperty(value = "营收增速最新季度PCT")
    private String revenuePCT;

    @ApiModelProperty(value = "营收增速最新季度Score")
    private String revenueScore;

    @ApiModelProperty(value = "净利增速最新季度PCT")
    private String netProfitPCT;

    @ApiModelProperty(value = "净利增速最新季度Score")
    private String netProfitScore;

}
