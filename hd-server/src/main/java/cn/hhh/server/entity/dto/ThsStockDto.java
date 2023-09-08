package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺-股票表
 * @Author HHH
 * @Date 2023/8/29 21:22
 */
@Data
@ApiModel("同花顺-股票表")
@AllArgsConstructor
@NoArgsConstructor
public class ThsStockDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "同花顺证券代码")
    private String thsStockCode;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "证券简称")
    private String stockShortName;

    @ApiModelProperty(value = "公司性质(同花顺)")
    private String corpNature;

    @ApiModelProperty(value = "一级中信行业代码")
    private String firstCiticIndustryCode;

    @ApiModelProperty(value = "一级中信行业")
    private String firstCiticIndustry;

    @ApiModelProperty(value = "二级中信行业代码")
    private String secondCiticIndustryCode;

    @ApiModelProperty(value = "二级中信行业")
    private String secondCiticIndustry;

    @ApiModelProperty(value = "三级中信行业代码")
    private String thirdCiticIndustryCode;

    @ApiModelProperty(value = "三级中信行业")
    private String thirdCiticIndustry;

    @ApiModelProperty(value = "一级申万行业代码")
    private String firstSwIndustryCode;

    @ApiModelProperty(value = "一级申万行业")
    private String firstSwIndustry;

    @ApiModelProperty(value = "二级申万行业代码")
    private String secondSwIndustryCode;

    @ApiModelProperty(value = "二级申万行业")
    private String secondSwIndustry;

    @ApiModelProperty(value = "三级申万行业代码")
    private String thirdSwIndustryCode;

    @ApiModelProperty(value = "三级申万行业")
    private String thirdSwIndustry;

    @ApiModelProperty(value = "一级长江行业代码")
    private String firstCjsIndustryCode;

    @ApiModelProperty(value = "一级长江行业")
    private String firstCjsIndustry;

    @ApiModelProperty(value = "二级长江行业代码")
    private String secondCjsIndustryCode;

    @ApiModelProperty(value = "二级长江行业")
    private String secondCjsIndustry;

    @ApiModelProperty(value = "三级长江行业代码")
    private String thirdCjsIndustryCode;

    @ApiModelProperty(value = "三级长江行业")
    private String thirdCjsIndustry;

    @ApiModelProperty(value = "四级长江行业代码")
    private String fourthCjsIndustryCode;

    @ApiModelProperty(value = "四级长江行业")
    private String fourthCjsIndustry;

    @ApiModelProperty(value = "一级同花顺行业代码")
    private String firstThsIndustryCode;

    @ApiModelProperty(value = "一级同花顺行业")
    private String firstThsIndustry;

    @ApiModelProperty(value = "二级同花顺行业代码")
    private String secondThsIndustryCode;

    @ApiModelProperty(value = "二级同花顺行业")
    private String secondThsIndustry;

    @ApiModelProperty(value = "三级同花顺行业代码")
    private String thirdThsIndustryCode;

    @ApiModelProperty(value = "三级同花顺行业")
    private String thirdThsIndustry;

    @ApiModelProperty(value = "状态 00：有效 01：无效")
    private String status;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
