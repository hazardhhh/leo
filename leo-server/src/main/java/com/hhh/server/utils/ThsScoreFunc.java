package com.hhh.server.utils;

import com.hhh.server.logger.LeoLog;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 5档评分函数
     *
     * @param pct
     * @return
     */
    public static Integer industryRankPct5(Double pct) {
        if (pct <= 0.2) {
            return 1;
        } else if (pct <= 0.4) {
            return 2;
        } else if (pct <= 0.6) {
            return 3;
        } else if (pct <= 0.8) {
            return 4;
        } else if (pct <= 1.0) {
            return 5;
        } else {
            // 这个else是特使得空值置空。如果在上面else，则空值可能也给最大值
            return null;
        }
    }

    /**
     * 10档评分函数
     *
     * @param pct
     * @return
     */
    public static Integer industryRankPct10(Double pct) {
        if (pct <= 0.1) {
            return 1;
        } else if (pct <= 0.2) {
            return 2;
        } else if (pct <= 0.3) {
            return 3;
        } else if (pct <= 0.4) {
            return 4;
        } else if (pct <= 0.5) {
            return 5;
        } else if (pct <= 0.6) {
            return 6;
        } else if (pct <= 0.7) {
            return 7;
        } else if (pct <= 0.8) {
            return 8;
        } else if (pct <= 0.9) {
            return 9;
        } else if (pct <= 1.0) {
            return 10;
        } else {
            // 这个else是特使得空值置空。如果在上面else，则空值可能也给最大值
            return null;
        }
    }

    /**
     * 10档评分函数取反
     *
     * @param pct
     * @return
     */
    public static Integer reIndustryRankPct10(Double pct) {
        if (pct <= 0.1) {
            return 10;
        } else if (pct <= 0.2) {
            return 9;
        } else if (pct <= 0.3) {
            return 8;
        } else if (pct <= 0.4) {
            return 7;
        } else if (pct <= 0.5) {
            return 6;
        } else if (pct <= 0.6) {
            return 5;
        } else if (pct <= 0.7) {
            return 4;
        } else if (pct <= 0.8) {
            return 3;
        } else if (pct <= 0.9) {
            return 2;
        } else if (pct <= 1.0) {
            return 1;
        } else {
            // 这个else是特使得空值置空。如果在上面else，则空值可能也给最大值
            return null;
        }
    }

    /**
     * 计算排名百分比
     *
     * @param value
     * @param list
     * @return
     */
    public static String rankPct(double value, List<Double> list) {
        int n = list.size();
        // 对列表进行排序
        List<Double> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        // 计算该值的排名百分比
        int rank = Collections.binarySearch(sortedList, value);
        if (rank < 0) {
            rank = -rank - 1;
        }
        double pct = (rank + 1.0) / (n + 1.0);
        // 创建 DecimalFormat 对象，指定保留小数点后 4 位
        DecimalFormat df = new DecimalFormat("0.0000");
        String formattedPct = df.format(pct);
        return formattedPct;
    }

    /**
     * MAD去极值函数
     *
     * @param factor
     * @param n
     * @return
     */
    public static double[] mad(double[] factor, double n) {
        // 求出因子值的中位数
        double median = getMedian(factor);
        // 求出因子值与中位数的差值, 进行绝对值
        double mad = getMedian(getAbsDiff(factor, median));
        // 定义几倍的中位数上下限
        double high = median + (n * 1.4826 * mad);
        double low = median - (n * 1.4826 * mad);
        // 替换上下限
        for (int i = 0; i < factor.length; i++) {
            if (factor[i] > high) {
                factor[i] = high;
            } else if (factor[i] < low) {
                factor[i] = low;
            }
        }
        return factor;
    }

    /**
     * 取中位数
     *
     * @param arr
     * @return
     */
    private static double getMedian(double[] arr) {
        Arrays.sort(arr);
        int len = arr.length;
        if (len % 2 == 0) {
            return (arr[len / 2] + arr[len / 2 - 1]) / 2;
        } else {
            return arr[len / 2];
        }
    }

    /**
     * 取绝对值
     *
     * @param arr
     * @param median
     * @return
     */
    private static double[] getAbsDiff(double[] arr, double median) {
        int len = arr.length;
        double[] diff = new double[len];
        for (int i = 0; i < len; i++) {
            diff[i] = Math.abs(arr[i] - median);
        }
        return diff;
    }

    public static void main(String[] args) throws ParseException {
        String dateString = "2011-04-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateString);
        System.out.println(getQuarterDate(date));
        System.out.println(industryRankPct10(2.0));
    }

}
