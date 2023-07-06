package com.hhh.server.mapper;

import com.hhh.server.pojo.ThsHotRes;

import java.util.List;

/**
 * @Description ThsHotMapper
 * @Author HHH
 * @Date 2023/6/8 21:51
 */
public interface ThsHotMapper {

    /**
     * 获取所有结果,日期
     *
     * @return
     */
    public List<ThsHotRes> getThsHotResultByDate(String date);

    /**
     * 批量新增同花顺个股人气表
     *
     * @param thsHotResList
     * @return
     */
    public int insertBatchThsHot(List<ThsHotRes> thsHotResList);

    /**
     * 获取所有一周平均值结果
     *
     * @return
     */
    public List<ThsHotRes> getThsHotAVGResultByWeek();

    /**
     * 获取所有一月平均值结果
     *
     * @return
     */
    public List<ThsHotRes> getThsHotAVGResultByMonth();

}
