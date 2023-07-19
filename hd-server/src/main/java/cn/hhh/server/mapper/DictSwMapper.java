package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.DictSwDto;

import java.util.List;

/**
 * @Description DictSwMapper
 * @Author HHH
 * @Date 2023/6/8 21:51
 */
public interface DictSwMapper {

    /**
     * 更新申万所属行业字典
     *
     * @param stockCode
     * @param firstIndustry
     * @param secondIndustry
     * @param thirdIndustry
     * @return
     */
    public int updateDictSw(String stockCode, String firstIndustry, String secondIndustry, String thirdIndustry);

    /**
     * 获取申万一级行业
     *
     * @return
     */
    public List<DictSwDto> getSwIndustry();

}
