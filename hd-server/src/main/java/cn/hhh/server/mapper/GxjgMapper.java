package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.GxjgDto;

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
    public List<GxjgDto> getGxjgResult(String date);

    /**
     * 获取所有结果,当月和上个月,详情信息
     *
     * @param time 1 当月 2 上个月
     * @param stockCode 证券代码
     * @return
     */
    public List<GxjgDto> getGxjgResultDetails(int time, String stockCode);

}
