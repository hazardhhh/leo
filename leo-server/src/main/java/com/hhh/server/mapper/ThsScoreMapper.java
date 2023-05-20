package com.hhh.server.mapper;

import com.hhh.server.pojo.ThsScoreRes;

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
    public List<ThsScoreRes> getThsScoreResult();

    /**
     * 更新股息率近12个月
     *
     * @param stockCode
     * @param dividend12
     * @return
     */
    public int updateThsScoreDividend12(String stockCode, String dividend12);

    /**
     * 更新股息率2021，2020，过去3年平均值
     *
     * @param stockCode
     * @param dividend2021
     * @param dividend2020
     * @param dividendAverage3
     * @return
     */
    public int updateThsScoreDividend(String stockCode, String dividend2021, String dividend2020, String dividendAverage3);
}
