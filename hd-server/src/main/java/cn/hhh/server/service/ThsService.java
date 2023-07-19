package cn.hhh.server.service;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.page.BasicPageRes;
import cn.hhh.server.entity.dto.ThsScoreDto;
import cn.hhh.server.entity.vo.ths.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description ThsScoreService服务类
 * @Author HHH
 * @Date 2023/5/19 22:48
 */
public interface ThsService {

    /**
     * 获取所有结果
     *
     * @return
     */
    List<ThsScoreDto> getThsScoreResult();

    /**
     * 更新股息率近12个月
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreDividend12Data();

    /**
     * 更新股息率2021，2020，过去3年平均值
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreDividendData();

    /**
     * 更新PB最新
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScorePBData();

    /**
     * 更新周换手率
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreTurnoverRateByWeekData();

    /**
     * 更新自由流通市值,总市值
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreMarketValueData();

    /**
     * 更新ROE最新季度
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreROEByQuarterData();

    /**
     * 更新ROE2022,2021,2020,过去3年平均值
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreROEData();

    /**
     * 更新营收增速,净利增速最新季度
     *
     * @return
     */
    BasicServiceModel<Object> updateThsScoreRevenueAndNetProfitData();

    /**
     * 计算排名百分比和打分
     *
     * @return
     */
    BasicServiceModel<BasicPageRes> rankScore(GetRankScoreVo getRankScoreVo, HttpServletResponse response);

    /**
     * 测试接口
     *
     * @return
     */
    BasicServiceModel<Object> thsTest();

    /**
     * 新增同花顺预测评级,机构预测明细表
     *
     * @return
     */
    BasicServiceModel<Object> insertThsMemoir(boolean taskByWeek);

    /**
     * 获取市场情绪因子,卖方研报信息
     *
     * @return
     */
    BasicServiceModel<BasicPageRes> getSellerStudieData(GetSellerStudieVo getSellerStudieVo, HttpServletResponse response);

    /**
     * 获取市场情绪因子,卖方研报信息详情
     *
     * @return
     */
    BasicServiceModel<Object> getSellerStudieDataDetails(GetSellerStudieDetailsVo getSellerStudieDetailsVo);

    /**
     * 获取市场情绪因子,卖方研报信息，国信金工被券商金股覆盖详情
     *
     * @return
     */
    BasicServiceModel<Object> getSellerStudieDataDetailsByGxjg(GetSellerStudieDetailsByGxjgVo getSellerStudieDetailsByGxjgVo);

    /**
     * 更新申万所属行业字典
     *
     * @return
     */
    BasicServiceModel<Object> updateDictSwData();

    /**
     * 新增同花顺用户行为统计表
     *
     * @return
     */
    BasicServiceModel<Object> insertThsBehavior();

    /**
     * 获取用户行为统计信息
     *
     * @return
     */
    BasicServiceModel<BasicPageRes> getThsBehaviorData(GetThsBehaviorVo getThsBehaviorVo, HttpServletResponse response);

    /**
     * 新增同花顺个股人气表
     *
     * @return
     */
    BasicServiceModel<Object> insertThsHot();

    /**
     * 获取用户行为统计和个股人气信息排名
     *
     * @return
     */
    BasicServiceModel<BasicPageRes> getThsBehaviorAndHotRankData(GetThsBehaviorAndHotRankVo getThsBehaviorAndHotRankVo, HttpServletResponse response);

}
