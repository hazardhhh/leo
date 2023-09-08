package cn.hhh.server.entity.res.buyerAttention;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 行业属性
 * @Author HHH
 * @Date 2023/9/4 9:25
 */
@Data
@ApiModel("行业属性")
@AllArgsConstructor
@NoArgsConstructor
public class IndustryAttributeRes {

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "label")
    private String label;

    @ApiModelProperty(value = "子行业列表")
    private List<IndustryAttributeRes> children;

}
