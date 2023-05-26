package com.hhh.server.utils;

import com.hhh.server.logger.LeoLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description 同花顺打分相关函数
 * @Author HHH
 * @Date 2023/5/24 14:21
 */
public class ThsScoreFunc {

    private static final LeoLog log = LeoLog.getInstance();

    /**
     * 获取最新季度时间
     *
     * @param currentDate
     * @return
     */
    public static String getQuarterDate(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        if (month <= Calendar.MARCH) {
            // Q4: 四季度 12-31
            cal.set(Calendar.YEAR, year - 1);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DATE, 31);
        } else if (month >= Calendar.APRIL && month <= Calendar.JUNE) {
            // Q1: 一季度 03-31
            cal.set(Calendar.MONTH, Calendar.MARCH);
            cal.set(Calendar.DATE, 31);
        } else if (month >= Calendar.JULY && month <= Calendar.SEPTEMBER) {
            // Q2: 二季度 06-30
            cal.set(Calendar.MONTH, Calendar.JUNE);
            cal.set(Calendar.DATE, 30);
        } else {
            // Q3: 三季度 09-30
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            cal.set(Calendar.DATE, 30);
        }

        Date quarterEndDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        log.info("ThsScoreFunc | getQuarterDate | 最新季度时间 = {}", dateFormat.format(quarterEndDate));
        return dateFormat.format(quarterEndDate);
    }

    public static void main(String[] args) throws ParseException {
        String dateString = "2011-04-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateString);
        System.out.println(getQuarterDate(date));
    }
}
