package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.ThsScoreDto;

import java.util.List;

/**
 * @Description ThsScoreMapper
 * @Author HHH
 * @Date 2023/5/19 22:51
 */
public interface ThsScoreMapper {

    /**
     * 获取所有结果
     *
     * @return
     */
    List<ThsScoreDto> getThsScoreResult();

    /**
     * 获取所有结果
     *
     * @param stockCode
     * @return
     */
    List<ThsScoreDto> getThsScoreResultByStockCode(String stockCode);

    /**
     * 更新股息率近12个月
     *
     * @param stockCode
     * @param dividend12
     * @return
     */
    int updateThsScoreDividend12(String stockCode, String dividend12);

    /**
     * 更新股息率2021，2020，过去3年平均值
     *
     * @param stockCode
     * @param dividend2021
     * @param dividend2020
     * @param dividendAverage3
     * @return
     */
    int updateThsScoreDividend(String stockCode, String dividend2021, String dividend2020, String dividendAverage3);

    /**
     * 更新PB最新
     *
     * @param stockCode
     * @param PB
     * @return
     */
    int updateThsScorePB(String stockCode, String PB);

    /**
     * 更新周换手率
     *
     * @param stockCode
     * @param turnoverRateByWeek
     * @return
     */
    int updateThsScoreTurnoverRateByWeek(String stockCode, String turnoverRateByWeek);

    /**
     * 更新自由流通市值,总市值
     *
     * @param stockCode
     * @param freeFlowMarketValue
     * @param allMarketValue
     * @return
     */
    int updateThsScoreMarketValue(String stockCode, String freeFlowMarketValue, String allMarketValue);

    /**
     * 更新ROE最新季度
     *
     * @param stockCode
     * @param turnoverRateByWeek
     * @return
     */
    int updateThsScoreROEByQuarter(String stockCode, String ROEByQuarter);

    /**
     * 更新ROE2022,2021，2020，过去3年平均值
     *
     * @param stockCode
     * @param ROE2022
     * @param ROE2021
     * @param ROE2020
     * @param ROEAverage3
     * @return
     */
    int updateThsScoreROE(String stockCode, String ROE2022, String ROE2021, String ROE2020, String ROEAverage3);

    /**
     * 更新营收增速,净利增速最新季度
     *
     * @param stockCode
     * @param revenue
     * @param netProfit
     * @return
     */
    int updateThsScoreRevenueAndNetProfit(String stockCode, String revenue, String netProfit);

}
