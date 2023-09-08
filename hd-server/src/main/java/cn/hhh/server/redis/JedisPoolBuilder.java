package cn.hhh.server.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * @Description JedisPoolBuilder
 * @Author HHH
 * @Date 2023/9/9 2:47
 */
public class JedisPoolBuilder {

    private String host;

    private int port;

    private String password;

    // 连接超时时间
    private int connectionTimeout;

    // 读取数据超时时间
    private int soTimeout;

    // 最大连接数
    private int maxTotal;

    // 最大空闲数
    private int maxIdle;

    public JedisPoolBuilder(Properties properties) {
        String clusterInfo = properties.getProperty("redis.cluster");
        String[] split = clusterInfo.split(",");
        String hostAndPort = split[0];
        int sepIndex = hostAndPort.indexOf(":");
        this.host = hostAndPort.substring(0, sepIndex);
        this.port = Integer.parseInt(hostAndPort.substring(sepIndex + 1));
        this.password = properties.getProperty("redis.password");
        this.connectionTimeout = Integer.valueOf(properties.getProperty("redis.connectionTimeout"));
        this.soTimeout = Integer.valueOf(properties.getProperty("redis.soTimeout"));
        this.maxTotal = Integer.valueOf(properties.getProperty("redis.maxTotal"));
        this.maxIdle = Integer.valueOf(properties.getProperty("redis.maxIdle"));
    }

    public JedisPool build() {
        JedisPool pool;
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.maxTotal);
        jedisPoolConfig.setMaxIdle(this.maxIdle);
        jedisPoolConfig.setTestOnReturn(true);
        if (this.password != null) {
            pool = new JedisPool(jedisPoolConfig, host, port, connectionTimeout, password);
        } else {
            pool = new JedisPool(jedisPoolConfig, host, port, connectionTimeout);
        }
        return pool;
    }

}
