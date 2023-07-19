package cn.hhh.server.mapper;

import cn.hhh.server.entity.vo.ths.TestRes;

import java.util.List;

/**
 * @Description DictSwMapper
 * @Author HHH
 * @Date 2023/6/8 21:51
 */
public interface TestMapper {

    /**
     * 获取数据
     *
     * @return
     */
    public List<TestRes> getTestData();

}
