package cn.hhh.server.logger;

/**
 * @Description LeoLog
 * @Author HHH
 * @Date 2023/5/27 2:01
 */
public class HdLog extends BasicLogger{
    @Override
    protected String getLogName() {
        return "hd";
    }

    private static final HdLog BASIC_LOG = new HdLog();

    private HdLog() {
    }

    public static HdLog getInstance() {
        return BASIC_LOG;
    }

}
