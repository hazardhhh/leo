package cn.hhh.server.entity.vo.ths;

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
@ApiModel("市场情绪因子,卖方研报详情")
@AllArgsConstructor
@NoArgsConstructor
public class GetSellerStudieDetailsVo {

    @ApiModelProperty(value = "时间 1周 2月")
    private Integer time;

    @ApiModelProperty(value = "是否深度报告")
    private boolean isDepthReport;

    @ApiModelProperty(value = "是否首次推荐")
    private boolean isFirstRecommend;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

}
