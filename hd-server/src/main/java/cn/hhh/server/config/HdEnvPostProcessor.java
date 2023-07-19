package cn.hhh.server.config;

import cn.hhh.server.logger.HdLog;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description 加载外部配置文件
 * @Author HHH
 * @Date 2023/5/27 1:58
 */
public class HdEnvPostProcessor implements EnvironmentPostProcessor {

    private static final HdLog log = HdLog.getInstance();

    private static final String PROPERTIES = "allProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        initLog4j2();
        initProp(environment);
    }

    /**
     * 初始化日志文件
     */
    private void initLog4j2() {
        String basicConfigPath = LocalConfig.configPath;
        // 日历log4j日志配置文件
        String log4jPath = basicConfigPath + File.separatorChar  + "log4j-hd.xml";
        File log4j = new File(log4jPath);
        if (log4j.exists()) {
            log.info("====== init custom log4j2 config ======");
            System.setProperty("logging.config", log4jPath);
        } else {
            log.info("====== init default log4j2 config ======");
            try {
                log4j = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + File.separatorChar + "hdConfig" + File.separatorChar + "log4j-hd.xml");
            } catch (FileNotFoundException e) {
                log.error("AdminEnvPostProcessor initLog4j2 error.", e);
            }
            if (log4j.exists()) {
                System.setProperty("logging.config", log4j.getAbsolutePath());
            }
        }

        log.info("log4j2 config path is {}", log4jPath);
    }

    private void initProp(ConfigurableEnvironment environment) {
        log.info("====== init properties ======");
        String basicConfigPath = LocalConfig.configPath;
        log.info("====== init custom properties ======");
        List<CustomConfig> propConfigs = new ArrayList<>();
        List<CustomConfig> xmlConfigs = new ArrayList<>();
        try {
            // 应用层格式的Apollo配置类
            String[] propertiesFiles = LocalConfig.CONFIG_FILES;
            if(ArrayUtils.isNotEmpty(propertiesFiles)){
                for (String fileName : propertiesFiles) {
                    //加载本地配置文件
                    if (StringUtils.endsWithIgnoreCase(fileName, ".xml")) {
                        //加载xml配置文件,不加载到内存中，只加载到apollo中
                        xmlConfigs.add(formatCustomConfig(fileName));
                    } else {
                        PropertiesUtil.loadPropertiesFileByPath(basicConfigPath + File.separatorChar + fileName);
                        propConfigs.add(formatCustomConfig(fileName));
                    }
                }
            }

            // 刷新配置至springContext
            refreshSpringContext(environment, PropertiesUtil.getPropertiesMap());
        } catch (Exception e) {
            log.error("init custom properties error.", e);
            System.exit(0);
        }

        refreshSpringContext(environment, PropertiesUtil.getRemotePropertiesMap());
        log.info("init properties finished.");
    }

    /**
     * 转换为Apollo的Config对象
     *
     * @param fileName 配置文件名称
     * @return 配置类
     */
    private CustomConfig formatCustomConfig(String fileName) {
        CustomConfig customConfig = new CustomConfig();
        String namespace = fileName.substring(0, fileName.lastIndexOf("."));
        customConfig.setFilename(fileName);
        customConfig.setNamespace(namespace);
        return customConfig;
    }

    /**
     * 刷新配置文件到spring上下文
     *
     * @param environment   环境信息
     * @param propertiesMap 所有配置map
     */
    private void refreshSpringContext(ConfigurableEnvironment environment, Map<String, String> propertiesMap) {
        if (CollectionUtils.isEmpty(propertiesMap)) {
            return;
        }
        MutablePropertySources propertySources = environment.getPropertySources();
        PropertiesPropertySource propertySource = (PropertiesPropertySource) propertySources.get(PROPERTIES);
        if (propertySource == null) {
            Properties properties = new Properties();
            properties.putAll(propertiesMap);
            propertySources.addFirst(new PropertiesPropertySource(PROPERTIES, properties));
        } else {
            propertySource.getSource().putAll(propertiesMap);
        }
    }

}
