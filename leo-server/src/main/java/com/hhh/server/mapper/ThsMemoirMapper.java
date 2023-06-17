package com.hhh.server.mapper;

import com.hhh.server.pojo.ThsMemoirRes;

import java.util.List;

/**
 * @Description ThsMemoirMapper
 * @Author HHH
 * @Date 2023/6/8 21:51
 */
public interface ThsMemoirMapper {

    /**
     * 获取所有结果,投资评级日期
     *
     * @return
     */
    public List<ThsMemoirRes> getThsMemoirResultByDate(String investmentRateDateNew);

    /**
     * 获取所有结果,最近一周
     *
     * @return
     */
    public List<ThsMemoirRes> getThsMemoirResultByWeek();

    /**
     * 获取所有结果,最近一个月
     *
     * @return
     */
    public List<ThsMemoirRes> getThsMemoirResultByMonth();

    /**
     * 新增同花顺预测评级,机构预测明细表
     *
     * @param memoirResList
     * @return
     */
    public int insertMemoir(ThsMemoirRes memoirResList);


    /**
     * 获取所有结果,最近一个月,详情信息
     *
     * @param time 1周 2月
     * @param isDepthReport 是否深度报告
     * @param isFirstRecommend 是否首次推荐
     * @param stockCode 证券代码
     * @return
     */
    public List<ThsMemoirRes> getThsMemoirResultDetails(int time, boolean isDepthReport, boolean isFirstRecommend, String stockCode);

}
