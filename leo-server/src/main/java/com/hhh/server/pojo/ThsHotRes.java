package com.hhh.server.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hhh
 * @since 2023-05-18
 */
@Data
@ApiModel("同花顺公司资料,同花顺人气,个股人气")
@AllArgsConstructor
@NoArgsConstructor
public class ThsHotRes {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "总热度（万）")
    private String hot;

    @ApiModelProperty(value = "游览热度（万）")
    private String visitHot;

    @ApiModelProperty(value = "热搜热度（万）")
    private String searchHot;

    @ApiModelProperty(value = "自选热度（万）")
    private String optionHot;

    @ApiModelProperty(value = "关注粘性（%）")
    private String focusTack;

}
