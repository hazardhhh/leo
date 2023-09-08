package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺-投资者活动关系记录明细表
 * @Author HHH
 * @Date 2023/8/28 9:00
 */
@Data
@ApiModel("同花顺-投资者活动关系记录明细表")
@AllArgsConstructor
@NoArgsConstructor
public class ThsInvestorActivitiesDetailDto {

    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "投资者活动关系记录表id")
    private String activitiesId;

    @ApiModelProperty(value = "参与单位名称")
    private String participateUnit;

    @ApiModelProperty(value = "参与单位类别")
    private String participateUnitType;

    @ApiModelProperty(value = "参与人员姓名")
    private String participants;

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
