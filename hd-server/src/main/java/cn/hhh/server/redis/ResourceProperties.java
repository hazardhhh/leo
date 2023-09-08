package cn.hhh.server.redis;

import cn.hhh.server.config.LocalConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description ResourceProperties
 * @Author HHH
 * @Date 2023/9/9 2:33
 */
public class ResourceProperties {

    private static final Properties props = new Properties();

    static{
        try {
            props.load(getPropertiesIsByPath(LocalConfig.configPath+ "/cache.conf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperty(){
        return props;
    }

    public static String getProperty(String key){
        return props.getProperty(key);
    }

    public static String getProperty(String key,String dv){
        String propertyVal = props.getProperty(key);
        if (propertyVal == null) {
            return dv;
        }
        return propertyVal;
    }

    public static int getPropertyInt(String key,int dv){
        String propertyVal = props.getProperty(key);
        if (propertyVal == null) {
            return Integer.valueOf(dv);
        }
        return dv;
    }

    public static InputStream getPropertiesIsByPath(String filepath)  {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
