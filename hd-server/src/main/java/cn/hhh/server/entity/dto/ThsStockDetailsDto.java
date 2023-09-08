package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺-股票明细表
 * @Author HHH
 * @Date 2023/8/29 21:22
 */
@Data
@ApiModel("同花顺-股票明细表")
@AllArgsConstructor
@NoArgsConstructor
public class ThsStockDetailsDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "数据类型 1:重仓股票明细 2:全部股票明细")
    private int dataType;

    @ApiModelProperty(value = "基金代码")
    private String fundCode;

    @ApiModelProperty(value = "基金名称")
    private String fundName;

    @ApiModelProperty(value = "报告期")
    private String date;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "持股市值（万元）")
    private String marketValue;

    @ApiModelProperty(value = "投资类型")
    private String investmentType;

    @ApiModelProperty(value = "管理公司")
    private String company;

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
