package com.hhh.server.config;

import com.hhh.server.logger.LeoLog;
import com.hhh.server.logger.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description LocalConfig
 * @Author HHH
 * @Date 2023/5/27 2:14
 */
public class LocalConfig {

    private static final Log log = LeoLog.getInstance();

    private static LocalConfig localConfig = new LocalConfig();

    public static final String configPath;

    public static final String[] CONFIG_FILES;

    private Properties prop;

    private String configDir;

    private String[] configFiles;

    public LocalConfig() {
        log.info("load LocalProperties Start========================================");

        try {
            InputStream is = this.getClass().getResourceAsStream("/local.properties");
            Throwable var2 = null;

            try {
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                Throwable var4 = null;

                try {
                    this.prop = new Properties();
                    this.prop.load(reader);
                    this.configDir = this.prop.getProperty("configdir");
                    String files = this.prop.getProperty("configFiles");
                    if (files != null && files.trim().length() > 0) {
                        this.configFiles = files.split(",");
                    }
                } catch (Throwable var29) {
                    var4 = var29;
                    throw var29;
                } finally {
                    if (reader != null) {
                        if (var4 != null) {
                            try {
                                reader.close();
                            } catch (Throwable var28) {
                                var4.addSuppressed(var28);
                            }
                        } else {
                            reader.close();
                        }
                    }

                }
            } catch (Throwable var31) {
                var2 = var31;
                throw var31;
            } finally {
                if (is != null) {
                    if (var2 != null) {
                        try {
                            is.close();
                        } catch (Throwable var27) {
                            var2.addSuppressed(var27);
                        }
                    } else {
                        is.close();
                    }
                }

            }
        } catch (Exception var33) {
            log.error("load LocalProperties Exception", new Object[]{var33});
        }

    }

    static {
        configPath = localConfig.configDir;
        CONFIG_FILES = localConfig.configFiles;
    }
}
