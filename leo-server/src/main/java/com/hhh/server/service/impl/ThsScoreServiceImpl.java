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
import java.util.ArrayList;
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

    @Override
    public List<ThsScoreRes> getThsScoreResult() {
        return thsScoreMapper.getThsScoreResult();
    }

    @Override
    public RespRes updateThsScore() {
        log.info("ThsScoreServiceImpl | updateThsScore | SystemProperty = {}", System.getProperty("java.library.path"));
        System.load("F://同花顺sdk//THSDataInterface_Windows//bin//x64//iFinDJava_x64.dll");
        int loginResultRet = -1;
        int tryCount = 3;
        while (tryCount > 0) {
            loginResultRet = JDIBridge.THS_iFinDLogin("mdzc121", "113259");
            log.info("ThsScoreServiceImpl | updateThsScore | loginResultRet = {}", loginResultRet);
            if (loginResultRet == 0) {
                try {
                    //获取所有股票代码
                    List<ThsScoreRes> thsScoreResList = thsScoreMapper.getThsScoreResult();
                    String[] stockCodes = thsScoreResList.stream()
                            .map(ThsScoreRes::getStockCode)
                            .toArray(String[]::new);
                    String stockCodesStr = String.join(",", stockCodes);
                    String dividend12Result = JDIBridge.THS_BasicData(stockCodesStr,"ths_dividend_rate_12m_stock", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    JSONObject jsonObject = JSON.parseObject(dividend12Result);
                    JSONArray tables = jsonObject.getJSONArray("tables");
                    JSONArray dividendRates = new JSONArray();
                    for (int i = 0; i < tables.size(); i++) {
                        JSONObject tableObj = tables.getJSONObject(i);
                        JSONObject table = tableObj.getJSONObject("table");
                        BigDecimal dividend = table.getJSONArray("ths_dividend_rate_12m_stock").getBigDecimal(0);
                        if (dividend == null) {
                            dividendRates.add(0.0000);
                        } else {
                            dividendRates.add(dividend.doubleValue());
                        }
                    }
                    for (int i = 0; i < dividendRates.size(); i++) {
                        Double dividend12 = dividendRates.getDouble(i);
                        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
                        DecimalFormat df = new DecimalFormat("0.0000");
                        String formattedDividend12 = df.format(dividend12);
                        thsScoreMapper.updateThsScore(stockCodes[i], formattedDividend12);
                        if (thsScoreMapper.updateThsScore(stockCodes[i], formattedDividend12) == 0) {
                            return RespRes.success("更新失败");
                        }
                    }
                } catch (Exception e) {
                    log.error("ThsScoreServiceImpl | updateThsScore | Exception", e);
                }
                JDIBridge.THS_iFinDLogout();
                log.info("ThsScoreServiceImpl updateThsScore THS_iFinDLogout");
                break;
            } else {
                log.info("ThsScoreServiceImpl | updateThsScore | LoginFailed | loginResultRet = {}", loginResultRet);
                tryCount--;
            }
        }

        if (tryCount == 0) {
            log.info("Failed to login after 3 tries.");
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
            ret = JDIBridge.THS_iFinDLogin("mdzc121", "113259");
            System.out.println(ret);
            if (ret == 0) {
                String strResultDataSerious = JDIBridge.THS_BasicData("000008.SZ","ths_dividend_rate_12m_stock","2023-05-20");
                System.out.println("result == " + strResultDataSerious );
                JSONObject jsonObject = JSON.parseObject(strResultDataSerious);
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
