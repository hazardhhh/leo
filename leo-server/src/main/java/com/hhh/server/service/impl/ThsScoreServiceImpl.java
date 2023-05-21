package com.hhh.server.service.impl;

import Ths.JDIBridge;
import com.hhh.server.mapper.ThsScoreMapper;
import com.hhh.server.pojo.RespRes;
import com.hhh.server.pojo.ThsScoreRes;
import com.hhh.server.service.ThsScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description ThsScoreServiceImpl实现类
 * @Author HHH
 * @Date 2023/5/19 22:50
 */
@Service
@Slf4j
public class ThsScoreServiceImpl implements ThsScoreService {

    @Autowired
    private ThsScoreMapper thsScoreMapper;

    /**
     * 同花顺用户名
     */
    private final String thsUserName = "xlkjyy001";

    /**
     * 同花顺密码
     */
    private final String thsPassWord = "666888";

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
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
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
                        log.info("ThsScoreServiceImpl | updateThsScoreDividend12Data | 股息率近12月 = {}", formattedDividend12Result);
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
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
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
                        log.info("ThsScoreServiceImpl | updateThsScoreDividendData | 股息率2021 = {} | 股息率2020 = {} | 股息率过去三年平均值 = {}", formattedDividend2021RateResult, formattedDividend2020RateResult, formattedDividendAverage3Result);
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
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
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
                        log.info("ThsScoreServiceImpl | updateThsScorePBData | PB最新 = {}", formattedPBResult);
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
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
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
                        log.info("ThsScoreServiceImpl | updateThsScoreTurnoverRateByWeekData | 周换手率 = {}", formattedTurnoverRateByWeekResult);
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
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
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
                        log.info("ThsScoreServiceImpl | updateThsScoreMarketValueData | 自由流通市值 = {} | 总市值 = {}", formattedFreeFlowMarketValueRateResult, formattedAllMarketValueRateResult);
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
                String result = JDIBridge.THS_BasicData("000001.SZ,000002.SZ,000006.SZ,000008.SZ","ths_market_value_stock","2023-05-21");
                System.out.println("result == " + result );
                JSONObject jsonObject = JSON.parseObject(result);
                System.out.println(jsonObject);
                JDIBridge.THS_iFinDLogout();
                System.out.println("THS_iFinDLogout");
                break;
            }
            else {
                System.out.println("Login failed ret == " + ret);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            System.out.println("Failed to login after 3 tries.");
        }

    }

}
