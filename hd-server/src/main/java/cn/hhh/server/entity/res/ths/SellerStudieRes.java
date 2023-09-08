package cn.hhh.server.entity.res.ths;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 市场情绪因子,卖方研报
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("市场情绪因子,卖方研报")
@AllArgsConstructor
@NoArgsConstructor
public class SellerStudieRes {

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "申万一级行业")
    private String swFirstIndustry;

    @ApiModelProperty(value = "深度报告过去一周")
    private String depthStudieByWeek;

    @ApiModelProperty(value = "深度报告过去一个月")
    private String depthStudieByMonth;

    @ApiModelProperty(value = "首次推荐过去一周")
    private String firstStudieByWeek;

    @ApiModelProperty(value = "首次推荐过去一个月")
    private String firstStudieByMonth;

    @ApiModelProperty(value = "被券商金股覆盖的数量当月")
    private String qsjgCoveredByTheMonth;

    @ApiModelProperty(value = "被券商金股覆盖的数量上个月")
    private String qsjgCoveredByLastMonth;

}
