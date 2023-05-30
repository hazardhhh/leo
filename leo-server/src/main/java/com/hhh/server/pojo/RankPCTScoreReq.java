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
@ApiModel("计算排名及打分实体类")
@AllArgsConstructor
@NoArgsConstructor
public class RankPCTScoreReq {

    @ApiModelProperty(value = "1 申万一级 2 申万一级及企业属性 3 申万二级 4 申万二级及企业属性")
    private Integer groupByType;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @ApiModelProperty(value = "申万属性")
    private String swAttr;

    @ApiModelProperty(value = "企业属性")
    private String corpAttr;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

}
