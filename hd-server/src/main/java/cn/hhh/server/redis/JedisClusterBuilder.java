package cn.hhh.server.redis;

import cn.hhh.server.constant.CacheConst;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @Description 构建redis集群实例
 * @Author HHH
 * @Date 2023/9/9 2:50
 */
public class JedisClusterBuilder {

    // 最大连接数
    private int maxTotal;

    // 最大空闲数
    private int maxIdle;

    // 最大允许等待时间
    private long maxWaitMillis;

    // 密码
    private String password;

    // 节点
    private Set<HostAndPort> nodes;

    // 连接超时时间
    private int connectionTimeout;

    // 读取数据超时时间
    private int soTimeout;

    // 最多尝试次数
    private int maxAttempts;

    public JedisClusterBuilder(Properties properties) {
        this.maxTotal = Integer.valueOf(properties.getProperty("redis.maxTotal"));
        this.maxIdle = Integer.valueOf(properties.getProperty("redis.maxIdle"));
        this.maxWaitMillis = Long.valueOf(properties.getProperty("redis.maxWaitMillis"));
        this.connectionTimeout = Integer.valueOf(properties.getProperty("redis.connectionTimeout"));
        this.soTimeout = Integer.valueOf(properties.getProperty("redis.soTimeout"));
        this.maxAttempts = Integer.valueOf(properties.getProperty("redis.maxAttempts"));
        this.password = properties.getProperty("redis.password");
        nodes = new LinkedHashSet<>();
        String clusterInfo = properties.getProperty("redis.cluster");
        String[] split = clusterInfo.split(",");
        for (String item : split) {
            int sepIndex = item.indexOf(":");
            HostAndPort hostAndPort = new HostAndPort(item.substring(0, sepIndex), Integer.valueOf(item.substring(sepIndex + 1)));
            nodes.add(hostAndPort);
        }
    }

    public JedisCluster build() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(this.maxTotal);
        // 最大空闲数
        poolConfig.setMaxIdle(this.maxIdle);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        poolConfig.setMaxWaitMillis(this.maxWaitMillis);
        //        JedisCluster cluster = new JedisCluster(this.nodes, poolConfig);
        JedisCluster cluster = new JedisCluster(this.nodes, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
        return cluster;
    }

    public static void main(String[] args) {
        Properties property = ResourceProperties.getProperty();
        String cacheMode = property.getProperty(CacheConst.CACHE_MODE);
        JedisCluster jedisCluster = new JedisClusterBuilder(property).build();
        jedisCluster.set("demo".getBytes(),"111".getBytes());
        jedisCluster.set(SerializableUtil.toBytes("11"), SerializableUtil.toBytes("11"));

        System.out.println(jedisCluster.get("demo".getBytes()));
        System.out.println(jedisCluster.get(SerializableUtil.toBytes("11")));
    }

}
