package cn.hhh.server.entity.res.buyerAttention;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 企业属性
 * @Author HHH
 * @Date 2023/9/4 9:43
 */
@Data
@ApiModel("企业属性")
@AllArgsConstructor
@NoArgsConstructor
public class CorpNatureRes {

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "label")
    private String label;

}
