package com.hhh.server.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺打分结果返回
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("同花顺打分结果实体类")
@AllArgsConstructor
@NoArgsConstructor
public class ThsScoreRes {

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "申万一级")
    private String swFirst;

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

}
