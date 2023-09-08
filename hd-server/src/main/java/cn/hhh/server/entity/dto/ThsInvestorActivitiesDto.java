package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺-投资者活动关系记录表
 * @Author HHH
 * @Date 2023/8/28 9:00
 */
@Data
@ApiModel("同花顺-投资者活动关系记录表")
@AllArgsConstructor
@NoArgsConstructor
public class ThsInvestorActivitiesDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "公告日期")
    private String publishDate;

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty(value = "活动内容简介")
    private String introduction;

    @ApiModelProperty(value = "状态 00：有效 01：无效")
    private String status;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
