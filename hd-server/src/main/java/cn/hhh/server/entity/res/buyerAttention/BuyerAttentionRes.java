package cn.hhh.server.entity.res.buyerAttention;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 买方关注度指标
 * @Author HHH
 * @Date 2023/8/28 9:27
 */
@Data
@ApiModel("买方关注度指标")
@AllArgsConstructor
@NoArgsConstructor
public class BuyerAttentionRes {

    @ApiModelProperty(value = "排名")
    private int rank;

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
    private String corpNature;

    @ApiModelProperty(value = "被调研次数近一周")
    private Long researchedFrequencyByWeek;

    @ApiModelProperty(value = "调研机构个数近一周")
    private Long researchedOrgCountByWeek;

    @ApiModelProperty(value = "调研人数近一周")
    private Long researchedPeopleNumByWeek;

    @ApiModelProperty(value = "被调研次数近一个月")
    private Long researchedFrequencyByMonth;

    @ApiModelProperty(value = "调研机构个数近一个月")
    private Long researchedOrgCountByMonth;

    @ApiModelProperty(value = "调研人数近一个月")
    private Long researchedPeopleNumByMonth;

    @ApiModelProperty(value = "前十大持仓公募基金数量（季度）")
    private Long getMutualFundCountByTopTen;

    @ApiModelProperty(value = "持仓公募基金数量（半年）")
    private Long getMutualFundCount;

    @ApiModelProperty(value = "被调研次数近一周得分")
    private String researchedFrequencyByWeekScore;

    @ApiModelProperty(value = "调研机构个数近一周得分")
    private String researchedOrgCountByWeekScore;

    @ApiModelProperty(value = "调研人数近一周得分")
    private String researchedPeopleNumByWeekScore;

    @ApiModelProperty(value = "被调研次数近一个月得分")
    private String researchedFrequencyByMonthScore;

    @ApiModelProperty(value = "调研机构个数近一个月得分")
    private String researchedOrgCountByMonthScore;

    @ApiModelProperty(value = "调研人数近一个月得分")
    private String researchedPeopleNumByMonthScore;

    @ApiModelProperty(value = "前十大持仓公募基金数量（季度）得分")
    private String getMutualFundCountByTopTenScore;

    @ApiModelProperty(value = "持仓公募基金数量（半年）得分")
    private String getMutualFundCountScore;

}
