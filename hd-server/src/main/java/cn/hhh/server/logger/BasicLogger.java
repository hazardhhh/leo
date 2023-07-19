package cn.hhh.server.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description BasicLogger
 * @Author HHH
 * @Date 2023/5/27 2:02
 */
public abstract class BasicLogger implements Log {

    private static final String MSG_SPLIT = " - ";

    private static final String PROPERTIES_SPLIT = " | ";

    private static final String CLASS_NAME = BasicLogger.class.getName();

    private Logger logger = null;

    public BasicLogger() {
    }

    protected abstract String getLogName();

    protected Logger getLogger() {
        String logName = this.getLogName();
        if (this.logger == null) {
            if (logName != null && !logName.isEmpty()) {
                this.logger = LoggerFactory.getLogger(logName);
            } else {
                this.logger = LoggerFactory.getLogger(this.getClass());
            }
        }

        return this.logger;
    }

    @Override
    public boolean isDebugEnable() {
        return this.getLogger().isDebugEnabled();
    }

    @Override
    public void debug(String messages) {
        this.log(LogLevelEnum.DEBUG, messages, (Object[])null);
    }

    @Override
    public void debug(String messages, Object... obj) {
        this.log(LogLevelEnum.DEBUG, messages, obj);
    }

    @Override
    public void info(String messages) {
        this.log(LogLevelEnum.INFO, messages, (Object[])null);
    }

    @Override
    public void info(String messages, Object... obj) {
        this.log(LogLevelEnum.INFO, messages, obj);
    }

    @Override
    public void warn(String messages) {
        this.log(LogLevelEnum.WARN, messages, (Object[])null);
    }

    @Override
    public void warn(String messages, Object... obj) {
        this.log(LogLevelEnum.WARN, messages, obj);
    }

    @Override
    public void error(String messages) {
        this.log(LogLevelEnum.ERROR, messages, (Object[])null);
    }

    @Override
    public void error(String messages, Object... obj) {
        this.log(LogLevelEnum.ERROR, messages, obj);
    }

    private void log(LogLevelEnum level, String message, Object[] obj) {
        if (level != LogLevelEnum.DEBUG || this.getLogger().isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            sb.append(this.getStackMsg(ste)).append(" - ");
            sb.append(message);
            if (level == LogLevelEnum.ERROR && obj != null && obj.length > 1) {
                Object errObj = obj[obj.length - 1];
                if (!message.contains("exception=") && errObj instanceof Throwable) {
                    Throwable t = (Throwable)errObj;
                    sb.append(" | ").append("exception=").append(t.getMessage());
                }
            }
            String fullMessage = sb.toString();
            switch (level) {
                case DEBUG:
                    this.getLogger().debug(fullMessage, obj);
                    break;
                case INFO:
                    this.getLogger().info(fullMessage, obj);
                    break;
                case WARN:
                    this.getLogger().warn(fullMessage, obj);
                    break;
                case ERROR:
                    this.getLogger().error(fullMessage, obj);
                    break;
                default:
                    this.getLogger().debug(fullMessage, obj);
            }

        }
    }

    protected String getStackMsg(StackTraceElement[] ste) {
        if (ste == null) {
            return null;
        } else {
            for(int i = 0; i < ste.length; ++i) {
                StackTraceElement s = ste[i];
                if (i > 0 && !CLASS_NAME.equals(s.getClassName())) {
                    String result = ste[i].toString();
                    return result.substring(result.indexOf("("));
                }
            }

            return null;
        }
    }
}
