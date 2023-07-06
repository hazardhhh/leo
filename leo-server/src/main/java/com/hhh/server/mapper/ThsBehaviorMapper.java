package com.hhh.server.mapper;

import com.hhh.server.pojo.ThsBehaviorRes;

import java.util.List;

/**
 * @Description ThsBehaviorMapper
 * @Author HHH
 * @Date 2023/6/8 21:51
 */
public interface ThsBehaviorMapper {

    /**
     * 获取所有结果,日期 类型 1 个股点击量前100 2 自选股留存前100 3 自选股增加前100 4 自选股剔除前100
     *
     * @return
     */
    public List<ThsBehaviorRes> getThsBehaviorResultByDateAndType(String date, String type);

    /**
     * 获取所有结果, 类型 1 个股点击量前100 2 自选股留存前100 3 自选股增加前100 4 自选股剔除前100
     *
     * @return
     */
    public List<ThsBehaviorRes> getThsBehaviorResultByTypeOfWeek(String type);

    /**
     * 获取所有结果, 类型 1 个股点击量前100 2 自选股留存前100 3 自选股增加前100 4 自选股剔除前100
     *
     * @return
     */
    public List<ThsBehaviorRes> getThsBehaviorResultByTypeOfMonth(String type);


    /**
     * 新增同花顺用户行为统计表
     *
     * @param behaviorRes
     * @return
     */
    public int insertThsBehavior(ThsBehaviorRes behaviorRes);

}
