package cn.hhh.server.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.logger.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @Description PropertiesUtil
 * @Author HHH
 * @Date 2023/7/9 11:03
 */
public class PropertiesUtil {

    private static final Log log = HdLog.getInstance();
    private static final String REGEX = "\\[(.*?)]";
    private static final String WEBCONF_PATH = "/hhh/hazard/thsMark/";
    private static final String NEWCONF_PATH = "/hhh/hazard/thsMark/";
    private static final Map<String, String> propertiesMap = new HashMap();
    private static final Map<String, String> remotePropertiesMap = new HashMap();

    private PropertiesUtil() {
    }

    public static Map<String, String> getRemotePropertiesMap() {
        return remotePropertiesMap;
    }

    public static Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public static boolean loadPropertiesFileByPath(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        } else {
            Properties properties = getPropertiesByPath(filePath);
            if (properties == null) {
                return false;
            } else {
                Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
                Iterator var3 = entrySet.iterator();

                while(var3.hasNext()) {
                    Map.Entry<Object, Object> entry = (Map.Entry)var3.next();
                    String key = String.valueOf(entry.getKey());
                    String value = String.valueOf(entry.getValue());
                    propertiesMap.put(key, value);
                }

                return true;
            }
        }
    }

    public static boolean loadPropertiesFileByLocalFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        } else {
            String filePath = getLocalFilePath(fileName);
            return filePath == null ? false : loadPropertiesFileByPath(filePath);
        }
    }

    private static String getLocalFilePath(String filename) {
        String localConfigPath = LocalConfig.configPath;
        String path = localConfigPath + File.separator + filename;
        if ((new File(path)).exists()) {
            return path;
        } else {
            path = "/hhh/hazard/thsMark/" + filename;
            return (new File(path)).exists() ? path : null;
        }
    }

    public static void addProperties(Properties properties) {
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        Iterator var2 = entrySet.iterator();

        while(var2.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry)var2.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            propertiesMap.put(key, value);
        }

    }

    public static void add(String key, String value) {
        propertiesMap.put(key, value);
    }

    public static Properties getPropertiesByPath(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        } else {
            Properties p = new Properties();

            try {
                InputStream inputStream = new FileInputStream(filepath);
                Throwable var4 = null;

                try {
                    p.load(inputStream);
                } catch (Throwable var14) {
                    var4 = var14;
                    throw var14;
                } finally {
                    if (inputStream != null) {
                        if (var4 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var13) {
                                var4.addSuppressed(var13);
                            }
                        } else {
                            inputStream.close();
                        }
                    }

                }
            } catch (Exception var16) {
                var16.printStackTrace();
            }

            return p;
        }
    }

    public static Map<String, String> getContainsPropertiesMap(String propName) {
        Map<String, String> resultMap = getContainsFromMap(propName, propertiesMap);
        resultMap.putAll(getContainsFromMap(propName, remotePropertiesMap));
        return resultMap;
    }

    private static Map<String, String> getContainsFromMap(String propName, Map<String, String> map) {
        Set<String> keySet = map.keySet();
        Map<String, String> propertiesMap = new HashMap();
        Iterator var4 = keySet.iterator();

        while(var4.hasNext()) {
            String key = (String)var4.next();
            if (key != null && key.contains(propName)) {
                propertiesMap.put(key, map.get(key));
            }
        }

        return propertiesMap;
    }

    public static String getProperty(String name) {
        if (StringUtils.isNotEmpty(name)) {
            String result = (String)remotePropertiesMap.get(name);
            if (result == null) {
                result = (String)propertiesMap.get(name);
            }

            return StringUtils.isNotEmpty(result) ? result.trim() : "";
        } else {
            return "";
        }
    }

    public static String getProperty(String name, String defaultValue) {
        String result = getProperty(name);
        return StringUtils.isNotEmpty(result) ? result : defaultValue;
    }

    public static List<String> getPropertyList(String namePrefix) {
        int i = 1;
        List<String> values = new ArrayList();

        while(true) {
            String key = namePrefix + i++;
            if (StringUtils.isEmpty(getProperty(key))) {
                return values;
            }

            values.add(getProperty(key));
        }
    }

    public static Integer getPropertiesInterger(String name) {
        return getPropertiesInterger(name, (Integer)null);
    }

    public static Integer getPropertiesInterger(String name, Integer dv) {
        String value = getProperty(name);
        return StringUtils.isEmpty(value) ? dv : Integer.valueOf(value);
    }

    public static void putInRemoteProperties(String key, String value) {
        remotePropertiesMap.put(key, value);
    }

    public static void removeForRemoteProperties(String key) {
        remotePropertiesMap.remove(key);
    }

    public static void removeForProperties(String key) {
        propertiesMap.remove(key);
    }

    public static void putAll(Map<String, String> map) {
        propertiesMap.putAll(map);
    }

    public static String removeSuffix(String fileName) {
        return fileName != null && !"".equals(fileName) && fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
    }

}
