package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.ThsHotDto;

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
    List<ThsHotDto> getThsHotResultByDate(String date);

    /**
     * 批量新增同花顺个股人气表
     *
     * @param thsHotDtoList
     * @return
     */
    int insertBatchThsHot(List<ThsHotDto> thsHotDtoList);

    /**
     * 获取所有一周平均值结果
     *
     * @return
     */
    List<ThsHotDto> getThsHotAVGResultByWeek();

    /**
     * 获取所有一月平均值结果
     *
     * @return
     */
    List<ThsHotDto> getThsHotAVGResultByMonth();

}
