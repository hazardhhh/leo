package com.hhh.server.service.impl;

import Ths.JDIBridge;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.hhh.server.logger.LeoLog;
import com.hhh.server.mapper.GxjgMapper;
import com.hhh.server.mapper.ThsMemoirMapper;
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
import cn.hutool.poi.excel.ExcelUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ThsMemoirMapper thsMemoirMapper;

    @Autowired
    private GxjgMapper gxjgMapper;

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
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreDividend12Data THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreDividendData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreDividendData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreDividendData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreDividendData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScorePBData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScorePBData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreTurnoverRateByWeekData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreTurnoverRateByWeekData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreMarketValueData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreMarketValueData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreROEByQuarterData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreROEByQuarterData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreROEData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreROEData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreROEData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreROEData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreROEData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreROEData THS_iFinDLogout");
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
                        log.info("ThsScoreServiceImpl updateThsScoreRevenueAndNetProfitData result null");
                        JDIBridge.THS_iFinDLogout();
                        log.info("ThsScoreServiceImpl updateThsScoreRevenueAndNetProfitData THS_iFinDLogout");
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
    public BasicPageRes<RespRes> rankPCTScore(RankPCTScoreReq rankPCTScoreReq, HttpServletResponse response) {
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
                thsScore.setDividend2020Score(dividend2020RankScore);
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
        List<ThsScoreRes> thsScoreResult;
        if (rankPCTScoreReq.getType() == 1) {
            thsScoreResult = thsScoreRes.stream()
                    .filter(result -> result.getStockCode().contains(rankPCTScoreReq.getKeyWord()) || result.getStockName().contains(rankPCTScoreReq.getKeyWord()))
                    .filter(result -> result.getSwFirst().contains(rankPCTScoreReq.getSwAttr()) || result.getSwSecond().contains(rankPCTScoreReq.getSwAttr()))
                    .filter(result -> result.getCorpAttr().contains(rankPCTScoreReq.getCorpAttr()))
                    .collect(Collectors.toList());
            // 导出excel
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.addHeaderAlias("stockCode","证券代码");
            writer.addHeaderAlias("stockName","证券名称");
            writer.addHeaderAlias("swFirst","申万一级");
            writer.addHeaderAlias("swSecond","申万二级");
            writer.addHeaderAlias("corpAttr","企业属性");
            writer.addHeaderAlias("dividend12","股息率近12个月");
            writer.addHeaderAlias("dividend2022","股息率2022");
            writer.addHeaderAlias("dividend2021","股息率2021");
            writer.addHeaderAlias("dividend2020","股息率2020");
            writer.addHeaderAlias("dividendAverage3","股息率过去3年平均值");
            writer.addHeaderAlias("PB","PB最新");
            writer.addHeaderAlias("turnoverRateByWeek","周换手率");
            writer.addHeaderAlias("freeFlowMarketValue","自由流通市值");
            writer.addHeaderAlias("allMarketValue","总市值");
            writer.addHeaderAlias("ROEByQuarter","ROE最新季度");
            writer.addHeaderAlias("ROE2022","ROE2022");
            writer.addHeaderAlias("ROE2021","ROE2021");
            writer.addHeaderAlias("ROE2020","ROE2020");
            writer.addHeaderAlias("ROEAverage3","ROE过去3年平均值");
            writer.addHeaderAlias("revenue","营收增速最新季度");
            writer.addHeaderAlias("netProfit","净利增速最新季度");
            writer.addHeaderAlias("dividend12PCT","股息率近12个月PCT");
            writer.addHeaderAlias("dividend12Score","股息率近12个月");
            writer.addHeaderAlias("dividend2022PCT","股息率2022PCT");
            writer.addHeaderAlias("dividend2022Score","股息率2022Score");
            writer.addHeaderAlias("dividend2021PCT","股息率2021PCT");
            writer.addHeaderAlias("dividend2021Score","股息率2021Score");
            writer.addHeaderAlias("dividend2020PCT","股息率2020PCT");
            writer.addHeaderAlias("dividend2020Score","股息率2020Score");
            writer.addHeaderAlias("dividendAverage3PCT","股息率过去3年平均值PCT");
            writer.addHeaderAlias("dividendAverage3Score","股息率过去3年平均值Score");
            writer.addHeaderAlias("PBPCT","PB最新PCT");
            writer.addHeaderAlias("PBScore","PB最新Score");
            writer.addHeaderAlias("turnoverRateByWeekPCT","周换手率PCT");
            writer.addHeaderAlias("turnoverRateByWeekScore","周换手率Score");
            writer.addHeaderAlias("freeFlowMarketValuePCT","自由流通市值PCT");
            writer.addHeaderAlias("freeFlowMarketValueScore","自由流通市值Score");
            writer.addHeaderAlias("allMarketValuePCT","总市值PCT");
            writer.addHeaderAlias("allMarketValueScore","总市值Score");
            writer.addHeaderAlias("ROEByQuarterPCT","ROE最新季度PCT");
            writer.addHeaderAlias("ROEByQuarterScore","ROE最新季度Score");
            writer.addHeaderAlias("ROE2022PCT","ROE2022PCT");
            writer.addHeaderAlias("ROE2022Score","ROE2022Score");
            writer.addHeaderAlias("ROE2021PCT","ROE2021PCT");
            writer.addHeaderAlias("ROE2021Score","ROE2021Score");
            writer.addHeaderAlias("ROE2020PCT","ROE2020PCT");
            writer.addHeaderAlias("ROE2020Score","ROE2020Score");
            writer.addHeaderAlias("ROEAverage3PCT","ROE过去3年平均值PCT");
            writer.addHeaderAlias("ROEAverage3Score","ROE过去3年平均值Score");
            writer.addHeaderAlias("revenuePCT","营收增速最新季度PCT");
            writer.addHeaderAlias("revenueScore","营收增速最新季度Score");
            writer.addHeaderAlias("netProfitPCT","净利增速最新季度PCT");
            writer.addHeaderAlias("netProfitScore","净利增速最新季度Score");
            writer.write(thsScoreResult, true);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition","attachment;filename=result.xls");
            ServletOutputStream out = null;
            try {
                out = response.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writer.flush(out, true);
            writer.close();
            IoUtil.close(out);
        }
        BasicPageRes<RespRes> res = new BasicPageRes<>();
        if (thsScoreRes.size() == 0) {
            res.setPage(null);
            res.setCode(500);
            res.setMessage("获取失败");
            res.setResult(new ArrayList<>());
            return res;
        }
        LeoPage leoPage = new LeoPage(rankPCTScoreReq.getPageNo(), rankPCTScoreReq.getPageSize());
        thsScoreResult = thsScoreRes.stream()
                .filter(result -> result.getStockCode().contains(rankPCTScoreReq.getKeyWord()) || result.getStockName().contains(rankPCTScoreReq.getKeyWord()))
                .filter(result -> result.getSwFirst().contains(rankPCTScoreReq.getSwAttr()) || result.getSwSecond().contains(rankPCTScoreReq.getSwAttr()))
                .filter(result -> result.getCorpAttr().contains(rankPCTScoreReq.getCorpAttr()))
                .skip((long) (rankPCTScoreReq.getPageNo() - 1) * rankPCTScoreReq.getPageSize())
                .limit(rankPCTScoreReq.getPageSize())
                .collect(Collectors.toList());
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
        res.setResult(thsScoreResult);
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

    /**
     * 新增同花顺预测评级,机构预测明细表
     *
     * @return
     */
    @Override
    public RespRes insertThsMemoir() {
        log.info("ThsScoreServiceImpl | insertThsMemoir | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load(thsLoadConf);
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin(thsUserName, thsPassWord);
            log.info("ThsScoreServiceImpl | insertThsMemoir | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    // 获取昨天的日期
                    String yesterdayStr = DateUtil.format(DateUtil.yesterday(), "yyyyMMdd");
                    LocalDate yesterday = LocalDate.parse(yesterdayStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
//                    LocalDate startDate = LocalDate.of(2023, 6, 10);
//                    LocalDate endDate = LocalDate.of(2023, 6, 10);
                    LocalDate startDate = yesterday;
                    LocalDate endDate = yesterday;
                    // 从开始日期开始遍历
                    LocalDate currentDate = startDate;
                    // 遍历到结束日期的下一天
                    while (currentDate.isBefore(endDate.plusDays(1))) {
                        String date = currentDate.toString().replace("-", "");
                        log.info("ThsScoreServiceImpl | insertThsMemoir | selectDate = {}", date);
                        //预测评级 机构预测明细
                        String result = JDIBridge.THS_DR("p00324","bgyear=2023;p0=最新;sdate=" + date + ";edate=" + date + ";blockFill=IFindKey@001005010@","jydm:Y,jydm_mc:Y,p00324_f001:Y,p00324_f002:Y,p00324_f036:Y,p00324_f037:Y,p00324_f045:Y,p00324_f003:Y,p00324_f004:Y,p00324_f005:Y,p00324_f038:Y,p00324_f039:Y,p00324_f006:Y,p00324_f007:Y,p00324_f008:Y,p00324_f009:Y,p00324_f010:Y,p00324_f011:Y,p00324_f012:Y,p00324_f013:Y,p00324_f014:Y,p00324_f046:Y,p00324_f047:Y,p00324_f048:Y,p00324_f015:Y,p00324_f016:Y,p00324_f017:Y,p00324_f018:Y,p00324_f019:Y,p00324_f020:Y,p00324_f021:Y,p00324_f022:Y,p00324_f023:Y,p00324_f024:Y,p00324_f025:Y,p00324_f026:Y,p00324_f027:Y,p00324_f028:Y,p00324_f029:Y,p00324_f030:Y,p00324_f033:Y");
                        JSONObject jsonObject = JSON.parseObject(result);
                        JSONArray tablesArray = jsonObject.getJSONArray("tables");
                        if (tablesArray.size() == 0) {
                            log.info("ThsScoreServiceImpl insertThsMemoir result null");
                            JDIBridge.THS_iFinDLogout();
                            log.info("ThsScoreServiceImpl insertThsMemoir THS_iFinDLogout");
                            return RespRes.error("获取失败");
                        }
                        List<ThsMemoirRes> memoirList = new ArrayList<>();
                        // 遍历 tables 数组，将数据解析为 Memoir 对象并添加到 memoirList 中
                        for (int i = 0; i < tablesArray.size(); i++) {
                            JSONObject tableData = tablesArray.getJSONObject(i).getJSONObject("table");
                            JSONArray jydmArray = tableData.getJSONArray("jydm");
                            JSONArray jydmMcArray = tableData.getJSONArray("jydm_mc");
                            JSONArray p00324_f001Array = tableData.getJSONArray("p00324_f001");
                            JSONArray p00324_f002Array = tableData.getJSONArray("p00324_f002");
                            JSONArray p00324_f036Array = tableData.getJSONArray("p00324_f036");
                            JSONArray p00324_f037Array = tableData.getJSONArray("p00324_f037");
                            JSONArray p00324_f045Array = tableData.getJSONArray("p00324_f045");
                            JSONArray p00324_f003Array = tableData.getJSONArray("p00324_f003");
                            JSONArray p00324_f004Array = tableData.getJSONArray("p00324_f004");
                            JSONArray p00324_f005Array = tableData.getJSONArray("p00324_f005");
                            JSONArray p00324_f038Array = tableData.getJSONArray("p00324_f038");
                            JSONArray p00324_f039Array = tableData.getJSONArray("p00324_f039");
                            JSONArray p00324_f006Array = tableData.getJSONArray("p00324_f006");
                            JSONArray p00324_f007Array = tableData.getJSONArray("p00324_f007");
                            JSONArray p00324_f008Array = tableData.getJSONArray("p00324_f008");
                            JSONArray p00324_f009Array = tableData.getJSONArray("p00324_f009");
                            JSONArray p00324_f010Array = tableData.getJSONArray("p00324_f010");
                            JSONArray p00324_f011Array = tableData.getJSONArray("p00324_f011");
                            JSONArray p00324_f012Array = tableData.getJSONArray("p00324_f012");
                            JSONArray p00324_f013Array = tableData.getJSONArray("p00324_f013");
                            JSONArray p00324_f014Array = tableData.getJSONArray("p00324_f014");
                            JSONArray p00324_f046Array = tableData.getJSONArray("p00324_f046");
                            JSONArray p00324_f047Array = tableData.getJSONArray("p00324_f047");
                            JSONArray p00324_f048Array = tableData.getJSONArray("p00324_f048");
                            JSONArray p00324_f015Array = tableData.getJSONArray("p00324_f015");
                            JSONArray p00324_f016Array = tableData.getJSONArray("p00324_f016");
                            JSONArray p00324_f017Array = tableData.getJSONArray("p00324_f017");
                            JSONArray p00324_f018Array = tableData.getJSONArray("p00324_f018");
                            JSONArray p00324_f019Array = tableData.getJSONArray("p00324_f019");
                            JSONArray p00324_f020Array = tableData.getJSONArray("p00324_f020");
                            JSONArray p00324_f021Array = tableData.getJSONArray("p00324_f021");
                            JSONArray p00324_f022Array = tableData.getJSONArray("p00324_f022");
                            JSONArray p00324_f023Array = tableData.getJSONArray("p00324_f023");
                            JSONArray p00324_f024Array = tableData.getJSONArray("p00324_f024");
                            JSONArray p00324_f025Array = tableData.getJSONArray("p00324_f025");
                            JSONArray p00324_f026Array = tableData.getJSONArray("p00324_f026");
                            JSONArray p00324_f027Array = tableData.getJSONArray("p00324_f027");
                            JSONArray p00324_f028Array = tableData.getJSONArray("p00324_f028");
                            JSONArray p00324_f029Array = tableData.getJSONArray("p00324_f029");
                            JSONArray p00324_f030Array = tableData.getJSONArray("p00324_f030");
                            JSONArray p00324_f033Array = tableData.getJSONArray("p00324_f033");
                            for (int j = 0; j < jydmArray.size(); j++) {
                                ThsMemoirRes thsMemoirRes = new ThsMemoirRes();
                                thsMemoirRes.setStockCode(jydmArray.getString(j));
                                thsMemoirRes.setStockName(jydmMcArray.getString(j));
                                thsMemoirRes.setResearchOrganization(p00324_f001Array.getString(j));
                                thsMemoirRes.setResearchers(p00324_f002Array.getString(j));
                                thsMemoirRes.setReportType(p00324_f036Array.getString(j));
                                thsMemoirRes.setReportName(p00324_f037Array.getString(j));
                                thsMemoirRes.setReportPages(p00324_f045Array.getString(j));
                                thsMemoirRes.setInvestmentRateNew(p00324_f003Array.getString(j));
                                thsMemoirRes.setInvestmentRatePre(p00324_f004Array.getString(j));
                                thsMemoirRes.setInvestmentRateAdjustDirection(p00324_f005Array.getString(j));
                                thsMemoirRes.setInvestmentRateTargetPrice(p00324_f038Array.getString(j));
                                thsMemoirRes.setInvestmentRateResearchDigest(p00324_f039Array.getString(j));
                                thsMemoirRes.setInvestmentRateDateNew(p00324_f006Array.getString(j));
                                thsMemoirRes.setEps1(p00324_f007Array.getString(j));
                                thsMemoirRes.setEps2(p00324_f008Array.getString(j));
                                thsMemoirRes.setEps3(p00324_f009Array.getString(j));
                                thsMemoirRes.setEps4(p00324_f010Array.getString(j));
                                thsMemoirRes.setEpsPredictDate(p00324_f011Array.getString(j));
                                thsMemoirRes.setEspIncreaseRate1(p00324_f012Array.getString(j));
                                thsMemoirRes.setEspIncreaseRate2(p00324_f013Array.getString(j));
                                thsMemoirRes.setEspIncreaseRate3(p00324_f014Array.getString(j));
                                thsMemoirRes.setPerShareNetProfitIncreaseRate1(p00324_f046Array.getString(j));
                                thsMemoirRes.setPerShareNetProfitIncreaseRate2(p00324_f047Array.getString(j));
                                thsMemoirRes.setPerShareNetProfitIncreaseRate3(p00324_f048Array.getString(j));
                                thsMemoirRes.setPE1(p00324_f015Array.getString(j));
                                thsMemoirRes.setPE2(p00324_f016Array.getString(j));
                                thsMemoirRes.setPE3(p00324_f017Array.getString(j));
                                thsMemoirRes.setPE4(p00324_f018Array.getString(j));
                                thsMemoirRes.setPEG1(p00324_f019Array.getString(j));
                                thsMemoirRes.setPEG2(p00324_f020Array.getString(j));
                                thsMemoirRes.setPEG3(p00324_f021Array.getString(j));
                                thsMemoirRes.setNetProfit1(p00324_f022Array.getString(j));
                                thsMemoirRes.setNetProfit2(p00324_f023Array.getString(j));
                                thsMemoirRes.setNetProfit3(p00324_f024Array.getString(j));
                                thsMemoirRes.setNetProfit4(p00324_f025Array.getString(j));
                                thsMemoirRes.setRevenue1(p00324_f026Array.getString(j));
                                thsMemoirRes.setRevenue2(p00324_f027Array.getString(j));
                                thsMemoirRes.setRevenue3(p00324_f028Array.getString(j));
                                thsMemoirRes.setRevenue4(p00324_f029Array.getString(j));
                                thsMemoirRes.setStockPriceDataNew(p00324_f030Array.getString(j));
                                thsMemoirRes.setDate(p00324_f033Array.getString(j));
                                memoirList.add(thsMemoirRes);
                            }
                        }//DateUtil.today()
                        List<ThsMemoirRes> thsMemoirResultByDateList = thsMemoirMapper.getThsMemoirResultByDate("2023/06/10");
                        if (memoirList.size() > 0) {
                            Set<String> existingStockCodes = new HashSet<>();
                            for (ThsMemoirRes memoir : thsMemoirResultByDateList) {
                                existingStockCodes.add(memoir.getStockCode());
                            }
                            for (ThsMemoirRes memoir : memoirList) {
                                if (!existingStockCodes.contains(memoir.getStockCode())) {
                                    try {
                                        thsMemoirMapper.insertMemoir(memoir);
                                        existingStockCodes.add(memoir.getStockCode());
                                    } catch (Exception e) {
                                        log.error("ThsScoreServiceImpl | insertThsMemoir | insertFail | Exception", e);
                                    }
                                }
                            }
                        }
                        // 遍历到下一个日期
                        currentDate = currentDate.plusDays(1);
                    }
                    log.info("ThsScoreServiceImpl insertThsMemoir insertEnd");
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | insertThsMemoir | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl insertThsMemoir THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | insertThsMemoir | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("ThsScoreServiceImpl insertThsMemoir Failed to login after 3 tries.");
        }

        return RespRes.success("插入成功");
    }

    /**
     * 获取市场情绪因子,卖方研报信息
     *
     * @return
     */
    @Override
    public BasicPageRes<RespRes> getSellerStudieData(SellerStudieReq sellerStudieReq, HttpServletResponse response) {
        List<ThsMemoirRes> memoirResListByWeek = thsMemoirMapper.getThsMemoirResultByWeek();
        List<ThsMemoirRes> memoirResListByMonth = thsMemoirMapper.getThsMemoirResultByMonth();
        List<SellerStudieRes> sellerStudieResList = new ArrayList<>();
        Map<String, String> depthStuideStockNameMap = new HashMap<>();
        Map<String, String> firstStudieStockNameMap = new HashMap<>();
        Map<String, Integer> depthStuideByWeekMap = new HashMap<>();
        Map<String, Integer> depthStuideByMonthMap = new HashMap<>();
        Map<String, Integer> firstStudieByWeekMap = new HashMap<>();
        Map<String, Integer> firstStudieByMonthMap = new HashMap<>();
        // 获取当月第一天日期
        String firstDayOfMonth = DateUtil.beginOfMonth(DateUtil.date()).toString("yyyyMMdd");
        List<GxjgRes> gxjgResListByTheMonth = gxjgMapper.getGxjgResult(firstDayOfMonth);
        // 获取上个月的第一天日期
        String firstDayOfLastMonth = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.date(), -1)).toString("yyyyMMdd");
        List<GxjgRes> gxjgResListByLastMonth = gxjgMapper.getGxjgResult(firstDayOfLastMonth);
        for (ThsMemoirRes thsMemoirRes : memoirResListByWeek) {
            // 判断是否深度报告
            if (Integer.parseInt(thsMemoirRes.getReportPages()) > 20) {
                String stockCode = thsMemoirRes.getStockCode();
                depthStuideByWeekMap.merge(stockCode, 1, Integer::sum);
            }
        }
        for (ThsMemoirRes thsMemoirRes : memoirResListByMonth) {
            // 判断是否深度报告
            if (Integer.parseInt(thsMemoirRes.getReportPages()) > 20) {
                String stockCode = thsMemoirRes.getStockCode();
                depthStuideStockNameMap.put(stockCode, thsMemoirRes.getStockName());
                depthStuideByMonthMap.merge(stockCode, 1, Integer::sum);
            }
        }
        for (ThsMemoirRes thsMemoirRes : memoirResListByWeek) {
            // 判断是否首次推荐
            if (thsMemoirRes.getReportName().contains("首次覆盖")) {
                String stockCode = thsMemoirRes.getStockCode();
                firstStudieByWeekMap.merge(stockCode, 1, Integer::sum);
            }
        }
        for (ThsMemoirRes thsMemoirRes : memoirResListByMonth) {
            // 判断是否首次推荐
            if (thsMemoirRes.getReportName().contains("首次覆盖")) {
                String stockCode = thsMemoirRes.getStockCode();
                firstStudieStockNameMap.put(stockCode, thsMemoirRes.getStockName());
                firstStudieByMonthMap.merge(stockCode, 1, Integer::sum);
            }
        }
        for (Map.Entry<String, Integer> entry : firstStudieByMonthMap.entrySet()) {
            String stockCode = entry.getKey();
            int firstStudieByMonth = entry.getValue();
            int firstStudieByWeek = firstStudieByWeekMap.getOrDefault(stockCode, 0);
            int depthStuideByWeek = depthStuideByWeekMap.getOrDefault(stockCode, 0);
            int depthStuideByMonth = depthStuideByMonthMap.getOrDefault(stockCode, 0);
            // 计算stockCode在gxjgResListByTheMonth和gxjgResListByLastMonth中出现的次数
            long qsjgCoveredByTheMonth = gxjgResListByTheMonth.stream().filter(gxjg -> gxjg.getStockCode().equals(stockCode)).count();
            long qsjgCoveredByLastMonth = gxjgResListByLastMonth.stream().filter(gxjg -> gxjg.getStockCode().equals(stockCode)).count();
            SellerStudieRes sellerStudieRes = new SellerStudieRes();
            sellerStudieRes.setStockCode(stockCode);
            sellerStudieRes.setStockName(firstStudieStockNameMap.get(stockCode));
            sellerStudieRes.setDepthStudieByWeek(String.valueOf(depthStuideByWeek));
            sellerStudieRes.setDepthStudieByMonth(String.valueOf(depthStuideByMonth));
            sellerStudieRes.setFirstStudieByWeek(String.valueOf(firstStudieByWeek));
            sellerStudieRes.setFirstStudieByMonth(String.valueOf(firstStudieByMonth));
            sellerStudieRes.setQsjgCoveredByTheMonth(String.valueOf(qsjgCoveredByTheMonth));
            sellerStudieRes.setQsjgCoveredByLastMonth(String.valueOf(qsjgCoveredByLastMonth));
            sellerStudieResList.add(sellerStudieRes);
        }
        for (Map.Entry<String, Integer> entry : depthStuideByMonthMap.entrySet()) {
            String stockCode = entry.getKey();
            if(!firstStudieByMonthMap.containsKey(stockCode)) {
                int depthStuideByMonth = entry.getValue();
                int depthStuideByWeek = depthStuideByWeekMap.getOrDefault(stockCode, 0);
                // 计算stockCode在gxjgResList中出现的次数
                long qsjgCoveredByTheMonth = gxjgResListByTheMonth.stream().filter(gxjg -> gxjg.getStockCode().equals(stockCode)).count();
                long qsjgCoveredByLastMonth = gxjgResListByLastMonth.stream().filter(gxjg -> gxjg.getStockCode().equals(stockCode)).count();
                SellerStudieRes sellerStudieRes = new SellerStudieRes();
                sellerStudieRes.setStockCode(stockCode);
                sellerStudieRes.setStockName(depthStuideStockNameMap.get(stockCode));
                sellerStudieRes.setDepthStudieByWeek(String.valueOf(depthStuideByWeek));
                sellerStudieRes.setDepthStudieByMonth(String.valueOf(depthStuideByMonth));
                sellerStudieRes.setFirstStudieByWeek("0");
                sellerStudieRes.setFirstStudieByMonth("0");
                sellerStudieRes.setQsjgCoveredByTheMonth(String.valueOf(qsjgCoveredByTheMonth));
                sellerStudieRes.setQsjgCoveredByLastMonth(String.valueOf(qsjgCoveredByLastMonth));
                sellerStudieResList.add(sellerStudieRes);
            }
        }
        log.info("ThsScoreServiceImpl | getSellerStudieData | sellerStudieResList = {}", sellerStudieResList);
        List<SellerStudieRes> sellerStudieResResult;
        if (sellerStudieReq.getType() == 1) {
            sellerStudieResResult = sellerStudieResList.stream()
                    .filter(result -> result.getStockCode().contains(sellerStudieReq.getKeyWord()) || result.getStockName().contains(sellerStudieReq.getKeyWord()))
                    .collect(Collectors.toList());
            // 导出excel
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.addHeaderAlias("stockCode","证券代码");
            writer.addHeaderAlias("stockName","证券名称");
            writer.addHeaderAlias("depthStudieByWeek","深度报告过去一周");
            writer.addHeaderAlias("depthStudieByMonth","深度报告过去一个月");
            writer.addHeaderAlias("firstStudieByWeek","首次推荐过去一周");
            writer.addHeaderAlias("firstStudieByMonth","首次推荐过去一个月");
            writer.addHeaderAlias("qsjgCoveredByTheMonth","被券商金股覆盖的数量当月");
            writer.addHeaderAlias("qsjgCoveredByLastMonth","被券商金股覆盖的数量上个月");
            writer.write(sellerStudieResResult, true);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition","attachment;filename=result.xls");
            ServletOutputStream out = null;
            try {
                out = response.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writer.flush(out, true);
            writer.close();
            IoUtil.close(out);
        }
        BasicPageRes<RespRes> res = new BasicPageRes<>();
        if (sellerStudieResList.size() == 0) {
            res.setPage(null);
            res.setCode(500);
            res.setMessage("获取失败");
            res.setResult(new ArrayList<>());
            return res;
        }
        LeoPage leoPage = new LeoPage(sellerStudieReq.getPageNo(), sellerStudieReq.getPageSize());
        sellerStudieResResult = sellerStudieResList.stream()
                .filter(result -> result.getStockCode().contains(sellerStudieReq.getKeyWord()) || result.getStockName().contains(sellerStudieReq.getKeyWord()))
                .skip((long) (sellerStudieReq.getPageNo() - 1) * sellerStudieReq.getPageSize())
                .limit(sellerStudieReq.getPageSize())
                .collect(Collectors.toList());
        int totalCount = (int) sellerStudieResList.stream()
                .filter(result -> result.getStockCode().contains(sellerStudieReq.getKeyWord()) || result.getStockName().contains(sellerStudieReq.getKeyWord()))
                .count();
        int totalPageSum = (int) Math.ceil((double) totalCount / sellerStudieReq.getPageSize());
        leoPage.setTotalCount((long) totalCount);
        leoPage.setTotalPageNum(totalPageSum);
        res.setPage(leoPage);
        res.setCode(200);
        res.setMessage("获取成功");
        res.setResult(sellerStudieResResult);
        return res;
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
//                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_np_yoy_stock","2023-03-31");
                // 预测评级 机构预测明细
                String result = JDIBridge.THS_DR("p00324","bgyear=2023;p0=最新;sdate=20230603;edate=20230603;blockFill=IFindKey@001005010@","jydm:Y,jydm_mc:Y,p00324_f001:Y,p00324_f002:Y,p00324_f036:Y,p00324_f037:Y,p00324_f045:Y,p00324_f003:Y,p00324_f004:Y,p00324_f005:Y,p00324_f038:Y,p00324_f039:Y,p00324_f006:Y,p00324_f007:Y,p00324_f008:Y,p00324_f009:Y,p00324_f010:Y,p00324_f011:Y,p00324_f012:Y,p00324_f013:Y,p00324_f014:Y,p00324_f046:Y,p00324_f047:Y,p00324_f048:Y,p00324_f015:Y,p00324_f016:Y,p00324_f017:Y,p00324_f018:Y,p00324_f019:Y,p00324_f020:Y,p00324_f021:Y,p00324_f022:Y,p00324_f023:Y,p00324_f024:Y,p00324_f025:Y,p00324_f026:Y,p00324_f027:Y,p00324_f028:Y,p00324_f029:Y,p00324_f030:Y,p00324_f033:Y");
                System.out.println("result == " + result );
                JSONObject jsonObject = JSON.parseObject(result);
//                System.out.println(jsonObject);
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
