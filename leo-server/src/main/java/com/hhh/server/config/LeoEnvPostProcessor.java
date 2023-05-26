package com.hhh.server.config;

import com.hhh.server.logger.LeoLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Description 加载外部配置文件
 * @Author HHH
 * @Date 2023/5/27 1:58
 */
public class LeoEnvPostProcessor implements EnvironmentPostProcessor {

    private static final LeoLog log = LeoLog.getInstance();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        initLog4j2();
    }

    /**
     * 初始化日志文件
     */
    private void initLog4j2() {
        String basicConfigPath = LocalConfig.configPath;
        // 日历log4j日志配置文件
        String log4jPath = basicConfigPath + File.separatorChar  + "log4j-leo.xml";
        File log4j = new File(log4jPath);
        if (log4j.exists()) {
            log.info("====== init custom log4j2 config ======");
            System.setProperty("logging.config", log4jPath);
        } else {
            log.info("====== init default log4j2 config ======");
            try {
                log4j = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + File.separatorChar + "leoConfig" + File.separatorChar + "log4j-leo.xml");
            } catch (FileNotFoundException e) {
                log.error("AdminEnvPostProcessor initLog4j2 error.", e);
            }
            if (log4j.exists()) {
                System.setProperty("logging.config", log4j.getAbsolutePath());
            }
        }

        log.info("log4j2 config path is {}", log4jPath);
    }

}
