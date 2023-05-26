package com.hhh.server.logger;

/**
 * @Description LeoLog
 * @Author HHH
 * @Date 2023/5/27 2:01
 */
public class LeoLog extends BasicLogger{
    @Override
    protected String getLogName() {
        return "thsServer";
    }

    private static final LeoLog leoLog = new LeoLog();

    private LeoLog() {
    }

    public static LeoLog getInstance() {
        return leoLog;
    }

}
