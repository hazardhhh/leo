package cn.hhh.server.entity.vo.buyerAttention;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 买方关注度指标数据Vo
 * @Author HHH
 * @Date 2023/8/30 22:15
 */
@Data
@ApiModel("买方关注度指标数据Vo")
@AllArgsConstructor
@NoArgsConstructor
public class GetBuyerAttentionVo {

    @ApiModelProperty(value = "类型 0 数值 1 分值")
    private Integer type;

    @ApiModelProperty(value = "证券代码/证券名称")
    private String keyWord;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

    @ApiModelProperty(value = "行业属性 1 申万一级 2 申万二级 3 申万三级")
    private Integer industryAttributeType;

    @ApiModelProperty(value = "行业属性")
    private List<String> industryAttribute;

    @ApiModelProperty(value = "企业属性")
    private List<String> corpNature;

    @ApiModelProperty(value = "打分策略 0 十分制 1 二十分制")
    private Integer rankTactics;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

}
