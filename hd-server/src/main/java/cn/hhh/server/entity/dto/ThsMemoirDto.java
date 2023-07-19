package cn.hhh.server.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 同花顺预测评级,机构预测明细
 * @Author HHH
 * @Date 2023/5/19 22:37
 */
@Data
@ApiModel("同花顺预测评级,机构预测明细")
@AllArgsConstructor
@NoArgsConstructor
public class ThsMemoirDto {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "证券代码")
    private String stockCode;

    @ApiModelProperty(value = "证券名称")
    private String stockName;

    @ApiModelProperty(value = "研究机构")
    private String researchOrganization;

    @ApiModelProperty(value = "研究人员")
    private String researchers;

    @ApiModelProperty(value = "报告类别")
    private String reportType;

    @ApiModelProperty(value = "报告名称")
    private String reportName;

    @ApiModelProperty(value = "报告页数")
    private String reportPages;

    @ApiModelProperty(value = "投资评级最新")
    private String investmentRateNew;

    @ApiModelProperty(value = "投资评级前次")
    private String investmentRatePre;

    @ApiModelProperty(value = "投资评级调整方向")
    private String investmentRateAdjustDirection;

    @ApiModelProperty(value = "投资评级目标价位")
    private String investmentRateTargetPrice;

    @ApiModelProperty(value = "投资评级研究摘要")
    private String investmentRateResearchDigest;

    @ApiModelProperty(value = "投资评级评级日期最新")
    private String investmentRateDateNew;

    @ApiModelProperty(value = "每股收益1")
    private String eps1;

    @ApiModelProperty(value = "每股收益2")
    private String eps2;

    @ApiModelProperty(value = "每股收益3")
    private String eps3;

    @ApiModelProperty(value = "每股收益4")
    private String eps4;

    @ApiModelProperty(value = "每股收益预测日期")
    private String epsPredictDate;

    @ApiModelProperty(value = "每股收益增长率1")
    private String espIncreaseRate1;

    @ApiModelProperty(value = "每股收益增长率2")
    private String espIncreaseRate2;

    @ApiModelProperty(value = "每股收益增长率3")
    private String espIncreaseRate3;

    @ApiModelProperty(value = "每股净利润增长率1")
    private String perShareNetProfitIncreaseRate1;

    @ApiModelProperty(value = "每股净利润增长率2")
    private String perShareNetProfitIncreaseRate2;

    @ApiModelProperty(value = "每股净利润增长率3")
    private String perShareNetProfitIncreaseRate3;

    @ApiModelProperty(value = "市盈率1")
    private String PE1;

    @ApiModelProperty(value = "市盈率2")
    private String PE2;

    @ApiModelProperty(value = "市盈率3")
    private String PE3;

    @ApiModelProperty(value = "市盈率4")
    private String PE4;

    @ApiModelProperty(value = "市盈增长比率1")
    private String PEG1;

    @ApiModelProperty(value = "市盈增长比率2")
    private String PEG2;

    @ApiModelProperty(value = "市盈增长比率3")
    private String PEG3;

    @ApiModelProperty(value = "净利润1")
    private String netProfit1;

    @ApiModelProperty(value = "净利润2")
    private String netProfit2;

    @ApiModelProperty(value = "净利润3")
    private String netProfit3;

    @ApiModelProperty(value = "净利润4")
    private String netProfit4;

    @ApiModelProperty(value = "主营业务收入1")
    private String revenue1;

    @ApiModelProperty(value = "主营业务收入2")
    private String revenue2;

    @ApiModelProperty(value = "主营业务收入3")
    private String revenue3;

    @ApiModelProperty(value = "主营业务收入4")
    private String revenue4;

    @ApiModelProperty(value = "股票最新收盘价")
    private String stockPriceDataNew;

    @ApiModelProperty(value = "获取日期")
    private String date;

}
