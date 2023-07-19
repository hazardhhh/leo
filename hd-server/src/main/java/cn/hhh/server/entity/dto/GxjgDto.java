package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 国信金工券商金股
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("国信金工券商金股")
@AllArgsConstructor
@NoArgsConstructor
public class GxjgDto {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "券商名称")
    private String securitiesName;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "发布日期")
    private String releaseDate;

    @ApiModelProperty(value = "具体时间")
    private String specificTime;

    @ApiModelProperty(value = "数据来源")
    private String dataSource;

}
