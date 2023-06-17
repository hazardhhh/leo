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
@ApiModel("市场情绪因子,卖方研报")
@AllArgsConstructor
@NoArgsConstructor
public class SellerStudieReq {

    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @ApiModelProperty(value = "申万属性")
    private String swAttr;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "0 查询 1 导出")
    private Integer type;

    @ApiModelProperty(value = "降序排序")
    private Integer sort;

}
