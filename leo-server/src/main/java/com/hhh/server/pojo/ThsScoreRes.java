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

}
