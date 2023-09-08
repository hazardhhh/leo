package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 申万所属行业字典
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("申万所属行业字典")
@AllArgsConstructor
@NoArgsConstructor
public class DictSwDto {

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "一级行业")
    private String firstIndustry;

    @ApiModelProperty(value = "二级行业")
    private String secondIndustry;

    @ApiModelProperty(value = "三级行业")
    private String thirdIndustry;

    @ApiModelProperty(value = "企业属性")
    private String enterpriseAttributes;

}
