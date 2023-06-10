package com.hhh.server.mapper;

import com.hhh.server.pojo.GxjgRes;

import java.util.List;

/**
 * @Description GxjzMapper
 * @Author HHH
 * @Date 2023/5/19 22:51
 */
public interface GxjgMapper {

    /**
     * 获取所有结果
     *
     * @return
     */
    public List<GxjgRes> getGxjgResult(String date);

}
