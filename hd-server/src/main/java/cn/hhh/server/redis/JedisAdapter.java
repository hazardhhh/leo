package cn.hhh.server.redis;

import java.util.Properties;

/**
 * @Description redis集群、单机适配
 * @Author HHH
 * @Date 2023/9/9 2:36
 */
public class JedisAdapter {

    private static final int REDIS_SINGLE  = 0;
    private static final int REDIS_CLUSTER = 1;
    private static final String REDIS_MODE = "redis.mode";

    public static ICache redisInstance(Properties property) throws Exception {
        // 默认单机
        Integer redisMode = property.get(REDIS_MODE) == null ? 0 : Integer.valueOf((String) property.get(REDIS_MODE));
        if (redisMode == REDIS_SINGLE) {
            System.out.println("cmd | cache = redis | mode = single ");
            return new JedisCacheImpl(new JedisPoolBuilder(property).build());
        } else if (redisMode == REDIS_CLUSTER) {
            System.out.println("cmd | cache = redis | mode = cluster ");
            return new JedisClusterCacheImpl(new JedisClusterBuilder(property).build());
        } else {
            throw new Exception("un support redis mode | redis.mode = " + redisMode);
        }
    }

}
