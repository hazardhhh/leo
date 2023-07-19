package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺公司资料,用户行为统计
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("同花顺公司资料,用户行为统计")
@AllArgsConstructor
@NoArgsConstructor
public class ThsBehaviorDto {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "排名")
    private String rank;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "DDE大单净额(万元)")
    private String DDENet;

    @ApiModelProperty(value = "DDE大单净量(%)")
    private String DDEBBD;

    @ApiModelProperty(value = "类型 1 个股点击量前100 2 自选股留存前100 3 自选股增加前100 4 自选股剔除前100")
    private String type;

}
