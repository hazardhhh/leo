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
@ApiModel("国信金工被券商金股覆盖详情")
@AllArgsConstructor
@NoArgsConstructor
public class SellerStudieDetailsByGxjgReq {

    @ApiModelProperty(value = "时间 1 当月 2 上个月")
    private Integer time;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

}
