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

    public List<ThsScoreRes> getThsScoreResult();

    public RespRes updateThsScore();
}
