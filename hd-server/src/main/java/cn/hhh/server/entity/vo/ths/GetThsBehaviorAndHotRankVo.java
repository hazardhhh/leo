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
@ApiModel("同花顺公司资料,用户行为统计,个股人气排名")
@AllArgsConstructor
@NoArgsConstructor
public class GetThsBehaviorAndHotRankVo {

    @ApiModelProperty(value = "类型 1 个股点击量前100 2 自选股留存前100 3 自选股增加前100 4 自选股剔除前100")
    private String selectType;

    @ApiModelProperty(value = "1 周 2 月")
    private Integer periodType;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "0 查询 1 导出")
    private Integer type;

}
