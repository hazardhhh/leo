package com.hhh.server.service;

import com.hhh.server.pojo.*;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 更新PB最新
     *
     * @return
     */
    public RespRes updateThsScorePBData();

    /**
     * 更新周换手率
     *
     * @return
     */
    public RespRes updateThsScoreTurnoverRateByWeekData();

    /**
     * 更新自由流通市值,总市值
     *
     * @return
     */
    public RespRes updateThsScoreMarketValueData();

    /**
     * 更新ROE最新季度
     *
     * @return
     */
    public RespRes updateThsScoreROEByQuarterData();

    /**
     * 更新ROE2022,2021,2020,过去3年平均值
     *
     * @return
     */
    public RespRes updateThsScoreROEData();

    /**
     * 更新营收增速,净利增速最新季度
     *
     * @return
     */
    public RespRes updateThsScoreRevenueAndNetProfitData();

    /**
     * 计算排名百分比和打分
     *
     * @return
     */
    public BasicPageRes<RespRes> rankPCTScore(RankPCTScoreReq rankPCTScoreReq, HttpServletResponse response);

    /**
     * 测试接口
     *
     * @return
     */
    public RespRes thsTest();

    /**
     * 新增同花顺预测评级,机构预测明细表
     *
     * @return
     */
    public RespRes insertThsMemoir(boolean taskByWeek);

    /**
     * 获取市场情绪因子,卖方研报信息
     *
     * @return
     */
    public BasicPageRes<RespRes> getSellerStudieData(SellerStudieReq sellerStudieReq, HttpServletResponse response);

    /**
     * 获取市场情绪因子,卖方研报信息详情
     *
     * @return
     */
    public RespRes getSellerStudieDataDetails(SellerStudieDetailsReq sellerStudieDetailsReq);

    /**
     * 获取市场情绪因子,卖方研报信息，国信金工被券商金股覆盖详情
     *
     * @return
     */
    public RespRes getSellerStudieDataDetailsByGxjg(SellerStudieDetailsByGxjgReq sellerStudieDetailsByGxjgReq);

    /**
     * 更新申万所属行业字典
     *
     * @return
     */
    public RespRes updateDictSwData();

}
