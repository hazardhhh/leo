package cn.hhh.server.logger;

/**
 * @Description Log
 * @Author HHH
 * @Date 2023/5/27 2:04
 */
public interface Log {

    boolean isDebugEnable();

    void debug(String var1);

    void debug(String var1, Object... var2);

    void info(String var1);

    void info(String var1, Object... var2);

    void warn(String var1);

    void warn(String var1, Object... var2);

    void error(String var1);

    void error(String var1, Object... var2);

}
