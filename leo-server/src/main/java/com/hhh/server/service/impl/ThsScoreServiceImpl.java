package com.hhh.server.service.impl;

import Ths.JDIBridge;
import com.hhh.server.logger.LeoLog;
import com.hhh.server.mapper.ThsScoreMapper;
import com.hhh.server.pojo.*;
import com.hhh.server.service.ThsScoreService;
import com.hhh.server.utils.ThsScoreFunc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description ThsScoreServiceImpl实现类
 * @Author HHH
 * @Date 2023/5/19 22:50
 */
@Service
public class ThsScoreServiceImpl implements ThsScoreService {

    private static final LeoLog log = LeoLog.getInstance();

    @Autowired
    private ThsScoreMapper thsScoreMapper;

    /**
     * 同花顺用户名
     */
    @Value("${ths.username}")
    private String thsUserName;

    /**
     * 同花顺密码
     */
    @Value("${ths.thsPassWord}")
    private String thsPassWord;

    /**
     * 同花顺加载配置
     */
//    private final String thsLoadConf = "F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll";
    @Value("${ths.loadConf}")
    private String thsLoadConf;

    /**
     * 获取所有结果
     *
     * @return
     */
    @Override
    public List<ThsScoreRes> getThsScoreResult() {
        return thsScoreMapper.getThsScoreResult();
    }

    /**
     * 更新股息率近12个月
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreDividend12Data() {
        log.info("ThsScoreServiceImpl | updateThsScoreDividend12Data | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreDividend12Data | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //股息率近12个月
                    String dividend12Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_dividend_rate_12m_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    JSONObject jsonObjectDividend12 = JSON.parseObject(dividend12Result);
                    JSONArray tablesDividend12 = jsonObjectDividend12.getJSONArray("tables");
                    JSONArray dividend12Rates = new JSONArray();
                    if (tablesDividend12.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesDividend12.size(); i++) {
                        JSONObject tableObj = tablesDividend12.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal dividend12 = table.getJSONArray("ths_dividend_rate_12m_stock").getBigDecimal(0);
                        if (dividend12 == null) {
                            dividend12Rates.add(0.0000);
                        } else {
                            dividend12Rates.add(dividend12.doubleValue());
                        }
                    }
                    //股息率近12个月
                    for (int i = 0; i < dividend12Rates.size(); i++) {
                        Double dividend12RateResult = dividend12Rates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedDividend12Result = df.format(dividend12RateResult);
                        log.info("ThsScoreServiceImpl | updateThsScoreDividend12Data | 证券代码 = {} | 股息率近12月 = {}", stockCodes[i], formattedDividend12Result);
                        thsScoreMapper.updateThsScoreDividend12(stockCodes[i], formattedDividend12Result);
                        if (thsScoreMapper.updateThsScoreDividend12(stockCodes[i], formattedDividend12Result) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreDividend12Data | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreDividend12Data THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreDividend12Data | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreDividend12Data Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新股息率2021，2020，过去3年平均值
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreDividendData() {
        log.info("ThsScoreServiceImpl | updateThsScoreDividendData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreDividendData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //股息率2021
                    String dividend2021Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_dividend_rate_stock_stock","2021-12-31,2021");
                    JSONObject jsonObjectDividend2021 = JSON.parseObject(dividend2021Result);
                    JSONArray tablesDividend2021 = jsonObjectDividend2021.getJSONArray("tables");
                    JSONArray dividend2021Rates = new JSONArray();
                    if (tablesDividend2021.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesDividend2021.size(); i++) {
                        JSONObject tableObj = tablesDividend2021.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal dividend = table.getJSONArray("ths_dividend_rate_stock_stock").getBigDecimal(0);
                        if (dividend == null) {
                            dividend2021Rates.add(0.0000);
                        } else {
                            dividend2021Rates.add(dividend.doubleValue());
                        }
                    }
                    //股息率2020
                    String dividend2020Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_dividend_rate_stock_stock","2020-12-31,2020");
                    JSONObject jsonObjectDividend2020 = JSON.parseObject(dividend2020Result);
                    JSONArray tablesDividend2020 = jsonObjectDividend2020.getJSONArray("tables");
                    JSONArray dividend2020Rates = new JSONArray();
                    if (tablesDividend2020.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesDividend2020.size(); i++) {
                        JSONObject tableObj = tablesDividend2020.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal dividend = table.getJSONArray("ths_dividend_rate_stock_stock").getBigDecimal(0);
                        if (dividend == null) {
                            dividend2020Rates.add(0.0000);
                        } else {
                            dividend2020Rates.add(dividend.doubleValue());
                        }
                    }
                    //股息率2021,2020,过去3年平均值
                    for (int i = 0; i < dividend2021Rates.size(); i++) {
                        Double dividend2021RateResult = dividend2021Rates.getDouble(i);
                        Double dividend2020RateResult = dividend2020Rates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedDividend2021RateResult = df.format(dividend2021RateResult);
                        String formattedDividend2020RateResult = df.format(dividend2020RateResult);
                        Double dividendAverage3 = (Double.parseDouble(thsScoreMapper.getThsScoreResult().get(i).getDividend2022()) + Double.parseDouble(formattedDividend2021RateResult) + Double.parseDouble(formattedDividend2020RateResult))/3;
                        String formattedDividendAverage3Result = df.format(dividendAverage3);
                        log.info("ThsScoreServiceImpl | updateThsScoreDividendData | 证券代码 = {} | 股息率2021 = {} | 股息率2020 = {} | 股息率过去三年平均值 = {}", stockCodes[i], formattedDividend2021RateResult, formattedDividend2020RateResult, formattedDividendAverage3Result);
                        thsScoreMapper.updateThsScoreDividend(stockCodes[i], formattedDividend2021RateResult, formattedDividend2020RateResult, formattedDividendAverage3Result);
                        if (thsScoreMapper.updateThsScoreDividend(stockCodes[i], formattedDividend2021RateResult, formattedDividend2020RateResult, formattedDividendAverage3Result) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreDividendData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreDividendData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreDividendData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreDividendData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新PB最新
     *
     * @return
     */
    @Override
    public RespRes updateThsScorePBData() {
        log.info("ThsScoreServiceImpl | updateThsScorePBData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScorePBData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //PB最新 100报告截止日期 101报告公告日期
                    String PBResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_pb_latest_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ",100");
                    JSONObject jsonObjectPB = JSON.parseObject(PBResult);
                    JSONArray tablesPB = jsonObjectPB.getJSONArray("tables");
                    JSONArray PBRates = new JSONArray();
                    if (tablesPB.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesPB.size(); i++) {
                        JSONObject tableObj = tablesPB.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal PB = table.getJSONArray("ths_pb_latest_stock").getBigDecimal(0);
                        if (PB == null) {
                            PBRates.add(0.0000);
                        } else {
                            PBRates.add(PB.doubleValue());
                        }
                    }
                    //PB最新
                    for (int i = 0; i < PBRates.size(); i++) {
                        Double PBRateResult = PBRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedPBResult = df.format(PBRateResult);
                        log.info("ThsScoreServiceImpl | updateThsScorePBData | 证券代码 = {} | PB最新 = {}", stockCodes[i], formattedPBResult);
                        thsScoreMapper.updateThsScorePB(stockCodes[i], formattedPBResult);
                        if (thsScoreMapper.updateThsScorePB(stockCodes[i], formattedPBResult) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScorePBData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScorePBData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScorePBData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScorePBData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新周换手率
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreTurnoverRateByWeekData() {
        log.info("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //更新周换手率
                    String turnoverRateByWeekResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_turnover_ratio_w_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    JSONObject jsonObjectTurnoverRateByWeek = JSON.parseObject(turnoverRateByWeekResult);
                    JSONArray tablesTurnoverRateByWeek = jsonObjectTurnoverRateByWeek.getJSONArray("tables");
                    JSONArray turnoverRateByWeekRates = new JSONArray();
                    if (tablesTurnoverRateByWeek.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesTurnoverRateByWeek.size(); i++) {
                        JSONObject tableObj = tablesTurnoverRateByWeek.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal turnoverRateByWeek = table.getJSONArray("ths_turnover_ratio_w_stock").getBigDecimal(0);
                        if (turnoverRateByWeek == null) {
                            turnoverRateByWeekRates.add(0.0000);
                        } else {
                            turnoverRateByWeekRates.add(turnoverRateByWeek.doubleValue());
                        }
                    }
                    //周换手率
                    for (int i = 0; i < turnoverRateByWeekRates.size(); i++) {
                        Double turnoverRateByWeekResultRateResult = turnoverRateByWeekRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedTurnoverRateByWeekResult = df.format(turnoverRateByWeekResultRateResult);
                        log.info("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | 证券代码 = {} | 周换手率 = {}", stockCodes[i], formattedTurnoverRateByWeekResult);
                        thsScoreMapper.updateThsScoreTurnoverRateByWeek(stockCodes[i], formattedTurnoverRateByWeekResult);
                        if (thsScoreMapper.updateThsScoreTurnoverRateByWeek(stockCodes[i], formattedTurnoverRateByWeekResult) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreTurnoverRateByWeekData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreTurnoverRateByWeekData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新自由流通市值，总市值
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreMarketValueData() {
        log.info("ThsScoreServiceImpl | updateThsScoreMarketValueData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreMarketValueData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //自由流通市值
                    String freeFlowMarketValueResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_free_float_mv_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    JSONObject jsonObjectFreeFlowMarketValue = JSON.parseObject(freeFlowMarketValueResult);
                    JSONArray tablesFreeFlowMarketValue = jsonObjectFreeFlowMarketValue.getJSONArray("tables");
                    JSONArray freeFlowMarketValueRates = new JSONArray();
                    if (tablesFreeFlowMarketValue.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesFreeFlowMarketValue.size(); i++) {
                        JSONObject tableObj = tablesFreeFlowMarketValue.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal freeFlowMarketValue = table.getJSONArray("ths_free_float_mv_stock").getBigDecimal(0);
                        if (freeFlowMarketValue == null) {
                            freeFlowMarketValueRates.add(0.0000);
                        } else {
                            freeFlowMarketValueRates.add(freeFlowMarketValue.doubleValue());
                        }
                    }
                    //总市值
                    String allMarketValueResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_market_value_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    JSONObject jsonObjectAllMarketValue = JSON.parseObject(allMarketValueResult);
                    JSONArray tablesAllMarketValue = jsonObjectAllMarketValue.getJSONArray("tables");
                    JSONArray allMarketValueRates = new JSONArray();
                    for (int i = 0; i < tablesAllMarketValue.size(); i++) {
                        JSONObject tableObj = tablesAllMarketValue.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal allMarketValue = table.getJSONArray("ths_market_value_stock").getBigDecimal(0);
                        if (allMarketValue == null) {
                            allMarketValueRates.add(0.0000);
                        } else {
                            allMarketValueRates.add(allMarketValue.doubleValue());
                        }
                    }
                    //自由流通市值,总市值
                    for (int i = 0; i < freeFlowMarketValueRates.size(); i++) {
                        Double freeFlowMarketValueRateResult = freeFlowMarketValueRates.getDouble(i);
                        Double allMarketValueRateResult = allMarketValueRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedFreeFlowMarketValueRateResult = df.format(freeFlowMarketValueRateResult);
                        String formattedAllMarketValueRateResult = df.format(allMarketValueRateResult);
                        log.info("ThsScoreServiceImpl | updateThsScoreMarketValueData | 证券代码 = {} | 自由流通市值 = {} | 总市值 = {}", stockCodes[i], formattedFreeFlowMarketValueRateResult, formattedAllMarketValueRateResult);
                        thsScoreMapper.updateThsScoreMarketValue(stockCodes[i], formattedFreeFlowMarketValueRateResult, formattedAllMarketValueRateResult);
                        if (thsScoreMapper.updateThsScoreMarketValue(stockCodes[i], formattedFreeFlowMarketValueRateResult, formattedAllMarketValueRateResult) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreMarketValueData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreMarketValueData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreMarketValueData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreMarketValueData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新ROE最新季度
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreROEByQuarterData() {
        log.info("ThsScoreServiceImpl | updateThsScoreROEByQuarterData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreROEByQuarterData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //更新ROE最新季度
                    String ROEByQuarterResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_sq_net_asset_yield_roe_stock", ThsScoreFunc.getQuarterDate(new Date()));
                    JSONObject jsonObjectROEByQuarter = JSON.parseObject(ROEByQuarterResult);
                    JSONArray tablesROEByQuarter = jsonObjectROEByQuarter.getJSONArray("tables");
                    JSONArray ROEByQuarterRates = new JSONArray();
                    if (tablesROEByQuarter.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesROEByQuarter.size(); i++) {
                        JSONObject tableObj = tablesROEByQuarter.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal ROEByQuarter = table.getJSONArray("ths_sq_net_asset_yield_roe_stock").getBigDecimal(0);
                        if (ROEByQuarter == null) {
                            ROEByQuarterRates.add(0.0000);
                        } else {
                            ROEByQuarterRates.add(ROEByQuarter.doubleValue());
                        }
                    }
                    //ROE最新季度
                    for (int i = 0; i < ROEByQuarterRates.size(); i++) {
                        Double ROEByQuarterResultRateResult = ROEByQuarterRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedROEByQuarter = df.format(ROEByQuarterResultRateResult);
                        log.info("ThsScoreServiceImpl | updateThsScoreROEByQuarterData | 证券代码 = {} | ROE最新季度 = {}", stockCodes[i], formattedROEByQuarter);
                        thsScoreMapper.updateThsScoreROEByQuarter(stockCodes[i], formattedROEByQuarter);
                        if (thsScoreMapper.updateThsScoreROEByQuarter(stockCodes[i], formattedROEByQuarter) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreROEByQuarterData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreROEByQuarterData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreROEByQuarterData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreROEByQuarterData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新ROE2022,2021,2020,过去3年平均值
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreROEData() {
        log.info("ThsScoreServiceImpl | updateThsScoreROEData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreROEData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //ROE2022
                    String ROE2022Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_roe_stock","20221231");
                    JSONObject jsonObjectROE2022 = JSON.parseObject(ROE2022Result);
                    JSONArray tablesROE2022 = jsonObjectROE2022.getJSONArray("tables");
                    JSONArray ROE2022Rates = new JSONArray();
                    if (tablesROE2022.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesROE2022.size(); i++) {
                        JSONObject tableObj = tablesROE2022.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal ROE2022 = table.getJSONArray("ths_roe_stock").getBigDecimal(0);
                        if (ROE2022 == null) {
                            ROE2022Rates.add(0.0000);
                        } else {
                            ROE2022Rates.add(ROE2022.doubleValue());
                        }
                    }
                    //ROE2021
                    String ROE2021Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_roe_stock","20211231");
                    JSONObject jsonObjectROE2021 = JSON.parseObject(ROE2021Result);
                    JSONArray tablesROE2021 = jsonObjectROE2021.getJSONArray("tables");
                    JSONArray ROE2021Rates = new JSONArray();
                    if (tablesROE2021.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesROE2021.size(); i++) {
                        JSONObject tableObj = tablesROE2021.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal ROE2021 = table.getJSONArray("ths_roe_stock").getBigDecimal(0);
                        if (ROE2021 == null) {
                            ROE2021Rates.add(0.0000);
                        } else {
                            ROE2021Rates.add(ROE2021.doubleValue());
                        }
                    }
                    //ROE2020
                    String ROE2020Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_roe_stock","20201231");
                    JSONObject jsonObjectROE2020 = JSON.parseObject(ROE2020Result);
                    JSONArray tablesROE2020 = jsonObjectROE2020.getJSONArray("tables");
                    JSONArray ROE2020Rates = new JSONArray();
                    if (tablesROE2020.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesROE2020.size(); i++) {
                        JSONObject tableObj = tablesROE2020.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal ROE2020 = table.getJSONArray("ths_roe_stock").getBigDecimal(0);
                        if (ROE2020 == null) {
                            ROE2020Rates.add(0.0000);
                        } else {
                            ROE2020Rates.add(ROE2020.doubleValue());
                        }
                    }
                    //ROE2022,2021,2020,过去3年平均值
                    for (int i = 0; i < ROE2022Rates.size(); i++) {
                        Double ROE2022RateResult = ROE2022Rates.getDouble(i);
                        Double ROE2021RateResult = ROE2021Rates.getDouble(i);
                        Double ROE2020RateResult = ROE2020Rates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedROE2022RateResult = df.format(ROE2022RateResult);
                        String formattedROE2021RateResult = df.format(ROE2021RateResult);
                        String formattedROE2020RateResult = df.format(ROE2020RateResult);
                        Double ROEAverage3 = (Double.parseDouble(formattedROE2022RateResult) + Double.parseDouble(formattedROE2021RateResult) + Double.parseDouble(formattedROE2020RateResult))/3;
                        String formattedROEAverage3Result = df.format(ROEAverage3);
                        log.info("ThsScoreServiceImpl | updateThsScoreROEData | 证券代码 = {} | ROE2022 = {} | ROE2021 = {} | ROE2020 = {} | ROE过去三年平均值 = {}", stockCodes[i], formattedROE2022RateResult, formattedROE2021RateResult, formattedROE2020RateResult, formattedROEAverage3Result);
                        thsScoreMapper.updateThsScoreROE(stockCodes[i], formattedROE2022RateResult, formattedROE2021RateResult, formattedROE2020RateResult, formattedROEAverage3Result);
                        if (thsScoreMapper.updateThsScoreROE(stockCodes[i], formattedROE2022RateResult, formattedROE2021RateResult, formattedROE2020RateResult, formattedROEAverage3Result) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreROEData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreROEData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreROEData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreROEData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 更新营收增速,净利增速最新季度
     *
     * @return
     */
    @Override
    public RespRes updateThsScoreRevenueAndNetProfitData() {
        log.info("ThsScoreServiceImpl | updateThsScoreRevenueAndNetProfitData | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | updateThsScoreRevenueAndNetProfitData | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    //营收增速最新季度
                    String revenueResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_or_yoy_stock", ThsScoreFunc.getQuarterDate(new Date()));
                    JSONObject jsonObjectRevenue = JSON.parseObject(revenueResult);
                    JSONArray tablesRevenue = jsonObjectRevenue.getJSONArray("tables");
                    JSONArray revenueRates = new JSONArray();
                    if (tablesRevenue.size() == 0) {
                        return RespRes.error("更新失败");
                    }
                    for (int i = 0; i < tablesRevenue.size(); i++) {
                        JSONObject tableObj = tablesRevenue.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal revenue = table.getJSONArray("ths_or_yoy_stock").getBigDecimal(0);
                        if (revenue == null) {
                            revenueRates.add(0.0000);
                        } else {
                            revenueRates.add(revenue.doubleValue());
                        }
                    }
                    //净利增速最新季度
                    String netProfitResult = JDIBridge.THS_BasicData(stockCodesStr,"ths_np_yoy_stock", ThsScoreFunc.getQuarterDate(new Date()));
                    JSONObject jsonObjectNetProfit = JSON.parseObject(netProfitResult);
                    JSONArray tablesNetProfit = jsonObjectNetProfit.getJSONArray("tables");
                    JSONArray netProfitRates = new JSONArray();
                    for (int i = 0; i < tablesNetProfit.size(); i++) {
                        JSONObject tableObj = tablesNetProfit.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal netProfit = table.getJSONArray("ths_np_yoy_stock").getBigDecimal(0);
                        if (netProfit == null) {
                            netProfitRates.add(0.0000);
                        } else {
                            netProfitRates.add(netProfit.doubleValue());
                        }
                    }
                    //营收增速,净利增速最新季度
                    for (int i = 0; i < revenueRates.size(); i++) {
                        Double revenueRateResult = revenueRates.getDouble(i);
                        Double netProfitRateResult = netProfitRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedRevenueRateResult = df.format(revenueRateResult);
                        String formattedNetProfitRateResult = df.format(netProfitRateResult);
                        log.info("ThsScoreServiceImpl | updateThsScoreRevenueAndNetProfitData | 证券代码 = {} | 营收增速最新季度 = {} | 净利增速最新季度 = {}", stockCodes[i], formattedRevenueRateResult, formattedNetProfitRateResult);
                        thsScoreMapper.updateThsScoreRevenueAndNetProfit(stockCodes[i], formattedRevenueRateResult, formattedNetProfitRateResult);
                        if (thsScoreMapper.updateThsScoreRevenueAndNetProfit(stockCodes[i], formattedRevenueRateResult, formattedNetProfitRateResult) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScoreRevenueAndNetProfitData | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScoreRevenueAndNetProfitData THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScoreRevenueAndNetProfitData | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl updateThsScoreRevenueAndNetProfitData Failed to login after 3 tries.");
        }

        return RespRes.success("更新成功");
    }

    /**
     * 计算排名百分比和打分
     *
     * @return
     */
    @Override
    public BasicPageRes<RespRes> rankPCTScore(RankPCTScoreReq rankPCTScoreReq) {
        List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
        //使用Collectors.groupingBy()方法将thsScoreResList按照swFirst字段进行分组
        Map<?, List<ThsScoreRes>> groupMap = null;
        if (rankPCTScoreReq.getGroupByType() == 1) {
            groupMap = thsScoreResList.stream().collect(Collectors.groupingBy(ThsScoreRes::getSwFirst));
        } else if (rankPCTScoreReq.getGroupByType() == 2) {
            groupMap = thsScoreResList.stream().collect(Collectors.groupingBy(res -> Tuple.of(res.getSwFirst(), res.getCorpAttr())));
        } else if (rankPCTScoreReq.getGroupByType() == 3) {
            groupMap = thsScoreResList.stream().collect(Collectors.groupingBy(ThsScoreRes::getSwSecond));
        } else if (rankPCTScoreReq.getGroupByType() == 4) {
            groupMap = thsScoreResList.stream().collect(Collectors.groupingBy(res -> Tuple.of(res.getSwSecond(), res.getCorpAttr())));
        } else {
            log.info("ThsScoreServiceImpl | rankPctAndScore | groupByType = {}", rankPCTScoreReq.getGroupByType());
            return null;
        }
        List<ThsScoreRes> thsScoreRes = new ArrayList<>();
        for (Map.Entry<?, List<ThsScoreRes>> entry : groupMap.entrySet()) {
            List<ThsScoreRes> groupList = null;
            if (rankPCTScoreReq.getGroupByType() == 1) {
                String swFirst = (String) entry.getKey();
                log.info("ThsScoreServiceImpl | rankPctAndScore | groupBy = {}", swFirst);
                groupList = entry.getValue();
            } else if (rankPCTScoreReq.getGroupByType() == 2) {
                Tuple2<String, String> compKey = (Tuple2<String, String>) entry.getKey();
                String swFirst = compKey._1;
                String corpAttr = compKey._2;
                log.info("ThsScoreServiceImpl | rankPctAndScore | groupBy1 = {} | groupBy2 = {}", swFirst, corpAttr);
                groupList = entry.getValue();
            } else if (rankPCTScoreReq.getGroupByType() == 3) {
                String swSecond = (String) entry.getKey();
                log.info("ThsScoreServiceImpl | rankPctAndScore | groupBy = {}", swSecond);
                groupList = entry.getValue();
            } else if (rankPCTScoreReq.getGroupByType() == 4) {
                Tuple2<String, String> compKey = (Tuple2<String, String>) entry.getKey();
                String swSecond = compKey._1;
                String corpAttr = compKey._2;
                log.info("ThsScoreServiceImpl | rankPctAndScore | groupBy1 = {} | groupBy2 = {}", swSecond, corpAttr);
                groupList = entry.getValue();
            } else {
                return null;
            }
            // 将每个元素的分数保存到一个列表中,并对列表进行排序
            // 股息率近12个月
            List<Double> dividend12List = groupList.stream()
                    .map(ThsScoreRes::getDividend12)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 股息率2022
            List<Double> dividend2022List = groupList.stream()
                    .map(ThsScoreRes::getDividend2022)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 股息率2021
            List<Double> dividend2021List = groupList.stream()
                    .map(ThsScoreRes::getDividend2021)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 股息率2020
            List<Double> dividend2020List = groupList.stream()
                    .map(ThsScoreRes::getDividend2020)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 股息率过去3年平均值
            List<Double> dividendAverage3List = groupList.stream()
                    .map(ThsScoreRes::getDividendAverage3)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // PB最新
            List<Double> PBList = groupList.stream()
                    .map(ThsScoreRes::getPB)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 周换手率
            List<Double> turnoverRateByWeekList = groupList.stream()
                    .map(ThsScoreRes::getTurnoverRateByWeek)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 自由流通市值
            List<Double> freeFlowMarketValueList = groupList.stream()
                    .map(ThsScoreRes::getFreeFlowMarketValue)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 总市值
            List<Double> allMarketValueList = groupList.stream()
                    .map(ThsScoreRes::getAllMarketValue)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // ROE最新季度
            List<Double> ROEByQuarterList = groupList.stream()
                    .map(ThsScoreRes::getROEByQuarter)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // ROE2022
            List<Double> ROE2022List = groupList.stream()
                    .map(ThsScoreRes::getROE2022)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // ROE2021
            List<Double> ROE2021List = groupList.stream()
                    .map(ThsScoreRes::getROE2021)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // ROE2020
            List<Double> ROE2020List = groupList.stream()
                    .map(ThsScoreRes::getROE2020)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // ROE过去3年平均值
            List<Double> ROEAverage3List = groupList.stream()
                    .map(ThsScoreRes::getROEAverage3)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 营收增速最新季度
            List<Double> revenueList = groupList.stream()
                    .map(ThsScoreRes::getRevenue)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 净利增速最新季度
            List<Double> netProfitList = groupList.stream()
                    .map(ThsScoreRes::getNetProfit)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            // 计算排名
//            int size = revenueList.size();
//            int[] ranks = new int[size];
//            for (int i = 0; i < size; i++) {
//                double value = revenueList.get(i);
//                int rank = 1;
//                for (int j = 0; j < size; j++) {
//                    if (i != j && revenueList.get(j) <= value) {
//                        rank++;
//                    }
//                }
//                ranks[i] = rank;
//            }
//            for (int i = 0; i < groupList.size(); i++) {
//                ThsScoreRes res = groupList.get(i);
//                double revenue = Double.parseDouble(res.getRevenue());
//                int revenueRank = ranks[revenueList.indexOf(revenue)];
//                log.info("ThsScoreServiceImpl | rankPctAndScore | stockCode = {} | revenueRank = {}", res.getStockCode(), revenueRank);
//            }
            // 计算排名百分比及得分
            for (ThsScoreRes res : groupList) {
                ThsScoreRes thsScore = new ThsScoreRes();
                String dividend12RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getDividend12()), dividend12List);
                String dividend12RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(dividend12RankPct)));
                String dividend2022RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getDividend2022()), dividend2022List);
                String dividend2022RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(dividend2022RankPct)));
                String dividend2021RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getDividend2021()), dividend2021List);
                String dividend2021RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(dividend2021RankPct)));
                String dividend2020RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getDividend2020()), dividend2020List);
                String dividend2020RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(dividend2020RankPct)));
                String dividendAverage3RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getDividendAverage3()), dividendAverage3List);
                String dividendAverage3RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(dividendAverage3RankPct)));
                // PB 越低越好 取反
                String PBRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getPB()), PBList);
                String PBRankScore = String.valueOf(ThsScoreFunc.reIndustryRankPct10(Double.valueOf(PBRankPct)));
                // turnoverRateByWeek 周换手率 越低越好 取反
                String turnoverRateByWeekRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getTurnoverRateByWeek()), turnoverRateByWeekList);
                String turnoverRateByWeekRankScore = String.valueOf(ThsScoreFunc.reIndustryRankPct10(Double.valueOf(turnoverRateByWeekRankPct)));
                // freeFlowMarketValue 自由流通市值 越低越好 取反
                String freeFlowMarketValueRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getFreeFlowMarketValue()), freeFlowMarketValueList);
                String freeFlowMarketValueRankScore = String.valueOf(ThsScoreFunc.reIndustryRankPct10(Double.valueOf(freeFlowMarketValueRankPct)));
                // allMarketValue 总市值 越低越好 取反
                String allMarketValueRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getAllMarketValue()), allMarketValueList);
                String allMarketValueRankScore = String.valueOf(ThsScoreFunc.reIndustryRankPct10(Double.valueOf(allMarketValueRankPct)));
                String ROEByQuarterRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getROEByQuarter()), ROEByQuarterList);
                String ROEByQuarterRankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(ROEByQuarterRankPct)));
                String ROE2022RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getROE2022()), ROE2022List);
                String ROE2022RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(ROE2022RankPct)));
                String ROE2021RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getROE2021()), ROE2021List);
                String ROE2021RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(ROE2021RankPct)));
                String ROE2020RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getROE2020()), ROE2020List);
                String ROE2020RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(ROE2020RankPct)));
                String ROEAverage3RankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getROEAverage3()), ROEAverage3List);
                String ROEAverage3RankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(ROEAverage3RankPct)));
                String revenueRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getRevenue()), revenueList);
                String revenueRankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(revenueRankPct)));
                String netProfitRankPct = ThsScoreFunc.rankPct(Double.parseDouble(res.getNetProfit()), netProfitList);
                String netProfitRankScore = String.valueOf(ThsScoreFunc.industryRankPct10(Double.valueOf(netProfitRankPct)));
                thsScore.setStockCode(res.getStockCode());
                thsScore.setStockName(res.getStockName());
                thsScore.setSwFirst(res.getSwFirst());
                thsScore.setSwSecond(res.getSwSecond());
                thsScore.setCorpAttr(res.getCorpAttr());
                thsScore.setDividend12(res.getDividend12());
                thsScore.setDividend2022(res.getDividend2022());
                thsScore.setDividend2021(res.getDividend2021());
                thsScore.setDividend2020(res.getDividend2020());
                thsScore.setDividendAverage3(res.getDividendAverage3());
                thsScore.setPB(res.getPB());
                thsScore.setTurnoverRateByWeek(res.getTurnoverRateByWeek());
                thsScore.setFreeFlowMarketValue(res.getFreeFlowMarketValue());
                thsScore.setAllMarketValue(res.getAllMarketValue());
                thsScore.setROEByQuarter(res.getROEByQuarter());
                thsScore.setROE2022(res.getROE2022());
                thsScore.setROE2021(res.getROE2021());
                thsScore.setROE2020(res.getROE2020());
                thsScore.setROEAverage3(res.getROEAverage3());
                thsScore.setRevenue(res.getRevenue());
                thsScore.setNetProfit(res.getNetProfit());
                // 排名与得分
                thsScore.setDividend12PCT(dividend12RankPct);
                thsScore.setDividend12Score(dividend12RankScore);
                thsScore.setDividend2022PCT(dividend2022RankPct);
                thsScore.setDividend2022Score(dividend2022RankScore);
                thsScore.setDividend2021PCT(dividend2021RankPct);
                thsScore.setDividend2021Score(dividend2021RankScore);
                thsScore.setDividend2020PCT(dividend2020RankPct);
                thsScore.setDividend2022Score(dividend2020RankScore);
                thsScore.setDividendAverage3PCT(dividendAverage3RankPct);
                thsScore.setDividendAverage3Score(dividendAverage3RankScore);
                thsScore.setPBPCT(PBRankPct);
                thsScore.setPBScore(PBRankScore);
                thsScore.setTurnoverRateByWeekPCT(turnoverRateByWeekRankPct);
                thsScore.setTurnoverRateByWeekScore(turnoverRateByWeekRankScore);
                thsScore.setFreeFlowMarketValuePCT(freeFlowMarketValueRankPct);
                thsScore.setFreeFlowMarketValueScore(freeFlowMarketValueRankScore);
                thsScore.setAllMarketValuePCT(allMarketValueRankPct);
                thsScore.setAllMarketValueScore(allMarketValueRankScore);
                thsScore.setROEByQuarterPCT(ROEByQuarterRankPct);
                thsScore.setROEByQuarterScore(ROEByQuarterRankScore);
                thsScore.setROE2022PCT(ROE2022RankPct);
                thsScore.setROE2022Score(ROE2022RankScore);
                thsScore.setROE2021PCT(ROE2021RankPct);
                thsScore.setROE2021Score(ROE2021RankScore);
                thsScore.setROE2020PCT(ROE2020RankPct);
                thsScore.setROE2020Score(ROE2020RankScore);
                thsScore.setROEAverage3PCT(ROEAverage3RankPct);
                thsScore.setROEAverage3Score(ROEAverage3RankScore);
                thsScore.setRevenuePCT(revenueRankPct);
                thsScore.setRevenueScore(revenueRankScore);
                thsScore.setNetProfitPCT(netProfitRankPct);
                thsScore.setNetProfitScore(netProfitRankScore);
                thsScoreRes.add(thsScore);
//                log.info("ThsScoreServiceImpl | rankPctAndScore | stockCode = {} | revenueRankPct = {} | revenueRankScore = {}", res.getStockCode(), revenueRankPct, revenueRankScore);
            }
        }
        log.info("ThsScoreServiceImpl | rankPctAndScore | rankPCTScoreRes = {}", thsScoreRes);
        BasicPageRes<RespRes> res = new BasicPageRes<>();
        if (thsScoreRes.size() == 0) {
            res.setPage(null);
            res.setCode(500);
            res.setMessage("获取失败");
            res.setVar(new ArrayList<>());
            return res;
        }
        LeoPage leoPage = new LeoPage(rankPCTScoreReq.getPageNo(), rankPCTScoreReq.getPageSize());
        List<ThsScoreRes> thsScoreResult = thsScoreRes.stream()
                .filter(result -> result.getStockCode().contains(rankPCTScoreReq.getKeyWord()) || result.getStockName().contains(rankPCTScoreReq.getKeyWord()))
                .filter(result -> result.getSwFirst().contains(rankPCTScoreReq.getSwAttr()) || result.getSwSecond().contains(rankPCTScoreReq.getSwAttr()))
                .filter(result -> result.getCorpAttr().contains(rankPCTScoreReq.getCorpAttr()))
                .skip((long) (rankPCTScoreReq.getPageNo() - 1) * rankPCTScoreReq.getPageSize())
                .limit(rankPCTScoreReq.getPageSize())
                .collect(Collectors.toList());res.setCode(200);
        int totalCount = (int) thsScoreRes.stream()
                .filter(result -> result.getStockCode().contains(rankPCTScoreReq.getKeyWord()) || result.getStockName().contains(rankPCTScoreReq.getKeyWord()))
                .filter(result -> result.getSwFirst().contains(rankPCTScoreReq.getSwAttr()) || result.getSwSecond().contains(rankPCTScoreReq.getSwAttr()))
                .filter(result -> result.getCorpAttr().contains(rankPCTScoreReq.getCorpAttr()))
                .count();
        int totalPageSum = (int) Math.ceil((double) totalCount / rankPCTScoreReq.getPageSize());
        leoPage.setTotalCount((long) totalCount);
        leoPage.setTotalPageNum(totalPageSum);
        res.setPage(leoPage);
        res.setCode(200);
        res.setMessage("获取成功");
        res.setVar(thsScoreResult);
        return res;
    }

    /**
     * 测试接口
     *
     * @return
     */
    @Override
    public RespRes thsTest() {
        log.info("ThsScoreServiceImpl | thsTest | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        String result = null;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | thsTest | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ", "ths_np_yoy_stock", "2023-03-31");
                log.info("ThsScoreServiceImpl | thsTest | result = {}", result);
                JSONObject jsonObjectResult = JSON.parseObject(result);
                log.info("ThsScoreServiceImpl | thsTest | jsonObjectResult = {}", jsonObjectResult);
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl thsTest THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | thsTest | LoginFail | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl thsTest Failed to login after 3 tries.");
        }

        return RespRes.success("获取成功", result);
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
        int ret = -1;
        if (args.length > 0) {
            System.out.println("login with cn account");
        }
        else {
            System.out.println("login with en account");
        }

        int tryCount = 3;
        while (tryCount > 0) {
            ret = JDIBridge.THS_iFinDLogin("xlkjyy001", "666888");
            System.out.println(ret);
            if (ret == 0) {
                //股息率近12个月
//                String result = JDIBridge.THS_BasicData("000008.SZ","ths_dividend_rate_12m_stock","2023-05-20");
                //股息率2021
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_dividend_rate_stock_stock","2021-12-31,2021");
                //PB最新
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_pb_latest_stock","2023-05-21,100");
                //周换手率
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_turnover_ratio_w_stock","2023-05-21");
                //自由流通市值
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_free_float_mv_stock","2023-05-21");
                //总市值
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_market_value_stock","2023-05-21");
                //ROE最新一季度
//                System.out.println(ThsScoreFunc.getQuarterDate(new Date()));
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_sq_net_asset_yield_roe_stock","2023-03-31");
                //ROE2022
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_roe_stock","20221231");
                //营收增速最新一季度
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_or_yoy_stock","20230331");
                //净利增速最新一季度
                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_np_yoy_stock","2023-03-31");
                System.out.println("result == " + result );
                JSONObject jsonObject = JSON.parseObject(result);
                System.out.println(jsonObject);
                JDIBridge.THS_iFinDLogout();
                System.out.println("THS_iFinDLogout");
                break;
            } else {
                System.out.println("Login failed ret == " + ret);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            System.out.println("Failed to login after 3 tries.");
        }

    }

}
