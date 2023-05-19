package com.hhh.server.mapper;

import com.hhh.server.pojo.ThsScoreRes;

import java.util.List;

/**
 * @Description ThsScoreMapper
 * @Author HHH
 * @Date 2023/5/19 22:51
 */
public interface ThsScoreMapper {

    public List<ThsScoreRes> getThsScoreResult();

    public int updateThsScore(String stockCode, String dividend12);
}
