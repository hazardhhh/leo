package com.hhh.server.service;

import com.hhh.server.pojo.RespRes;
import com.hhh.server.pojo.ThsScoreRes;

import java.util.List;

/**
 * @Description ThsScoreService服务类
 * @Author HHH
 * @Date 2023/5/19 22:48
 */
public interface ThsScoreService {

    /**
     * 获取所有结果
     *
     * @return
     */
    public List<ThsScoreRes> getThsScoreResult();

    /**
     * 更新股息率近12个月
     *
     * @return
     */
    public RespRes updateThsScoreDividend12Data();

    /**
     * 更新股息率2021，2020，过去3年平均值
     *
     * @return
     */
    public RespRes updateThsScoreDividendData();
}
