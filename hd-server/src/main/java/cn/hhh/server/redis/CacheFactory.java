package cn.hhh.server.redis;

import cn.hhh.server.constant.CacheConst;

import java.util.Properties;

/**
 * @Description CacheFactory
 * @Author HHH
 * @Date 2023/9/9 2:27
 */
public class CacheFactory {

    private static ICache cache;

    static {
        System.out.println("==init==");
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() throws Exception {
        cache = getCacheInstant();
    }

    public static ICache getCache() {
        return cache;
    }

    private static ICache getCacheInstant() throws Exception {
        Properties property = ResourceProperties.getProperty();
        String cacheMode = property.getProperty(CacheConst.CACHE_MODE);
        if (CacheConst.CACHE_REDIS.equalsIgnoreCase(cacheMode)) {
            return JedisAdapter.redisInstance(property);
        } else {
            return null;
        }
    }

}
