package cn.hhh.server.redis;

import cn.hhh.server.constant.CacheConst;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

import java.util.*;

/**
 * @Description redis集群模式实现
 * @Author HHH
 * @Date 2023/9/9 2:49
 */
public class JedisClusterCacheImpl extends BaseCache {

    private JedisCluster cluster;

    public JedisClusterCacheImpl(JedisCluster cluster) {
        this.cluster = cluster;
    }

    public void set(String key, Object value) {
        set(key, value, CacheConst.MAX_CACHE_TIME);
    }

    public Object get(String key) {
        // key 必须使用同一个序列化
        // key转化为字节，为了存储所有类型的value
        return SerializableUtil.toObject(cluster.get(SerializableUtil.toBytes(key)));
    }

    public List mget(List<String> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        // redis cluster模式下的mget命令要求keys都位于同一卡槽，否则报错 TODO
        List<Object> objects = new ArrayList<>();
        for (String key : keys) {
            Object val = get(key);
            if (val != null) {
                objects.add(get(key));
            }
        }
        return objects;
    }

    @Override
    public void replace(String key, Object value, int seconds) {
        this.del(key);
        this.set(key,value,seconds);
    }

    @Override
    public void set(String key, Object value, int seconds) {
        if (seconds < 1) {
            // < 1 is  illegal parameter
            seconds = 1;
        }
        SetParams setParams = new SetParams();
        setParams.ex(seconds);
        cluster.set(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value),setParams);
    }

    @Override
    public void replace(String key, Object value) {
        cluster.set(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
    }

    @Override
    public void append(String key, Object val) {
        cluster.append(SerializableUtil.toBytes(key),SerializableUtil.toBytes(val));
    }

    @Override
    public Object getAndTouch(String key, int seconds) {
        expire(key,seconds);
        return get(key);
    }

    public Object del(String key) {
        return cluster.del(SerializableUtil.toBytes(key));
    }

    @Override
    public void hDel(String cacheName) {
        del(cacheName);
    }

    public void expire(String key, int seconds) {
        if (seconds < 1) {
            // < 1 is  illegal parameter
            seconds = 1;
        }
        cluster.expire(SerializableUtil.toBytes(key), seconds);
    }

    public boolean contains(String key) {
        return cluster.exists(SerializableUtil.toBytes(key));
    }

    public void hSet(String cacheName, String key, Object value) {
        cluster.hset(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
    }

    public Object hGet(String cacheName, String key) {
        return SerializableUtil.toObject(cluster.hget(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key)));
    }

    @Override
    public List hVals(String cacheName) {
        log.info("hVals");
        Collection<byte[]> hvals = cluster.hvals(SerializableUtil.toBytes(cacheName));
        if (hvals == null || hvals.size() == 0) {
            return  Collections.emptyList();
        }
        return SerializableUtil.toObjectList(hvals);
    }

    public boolean hContains(String cacheName, String key) {
        return cluster.hexists(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key));
    }

    public Object hDel(String cacheName, String key) {
        return cluster.hdel(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key));
    }

    public Set<String> hKeys(String cacheName) {
        Set<byte[]> hkeys = cluster.hkeys(SerializableUtil.toBytes(cacheName));
        if (hkeys == null || hkeys.size() == 0) {
            return new HashSet<>();
        }

        Set<String> objects = new HashSet<>();
        for (byte[] aByte : hkeys) {
            objects.add((String) SerializableUtil.toObject(aByte));
        }
        return objects;
    }

    @Override
    public void lpush(String key, Object value) {
        cluster.lpush(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
    }

    @Override
    public Object lpop(String key) {
        return SerializableUtil.toObject(cluster.lpop(SerializableUtil.toBytes(key)));
    }

    @Override
    public void rpush(String key, Object value) {
        cluster.rpush(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
    }

    @Override
    public Object rpop(String key) {
        return SerializableUtil.toObject(cluster.rpop(SerializableUtil.toBytes(key)));
    }

}
