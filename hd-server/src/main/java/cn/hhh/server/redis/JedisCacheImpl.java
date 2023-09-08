package cn.hhh.server.redis;

import cn.hhh.server.constant.CacheConst;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.*;

/**
 * @Description redis单机模式
 * @Author HHH
 * @Date 2023/9/9 2:37
 */
public class JedisCacheImpl extends BaseCache {

    private static JedisPool pool;

    public JedisCacheImpl(JedisPool pool) {
        this.pool = pool;
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    private void releaseJedis(Jedis jedis){
        jedis.close();
    }


    public void set(String key, Object value) {
        set(key, value, CacheConst.MAX_CACHE_TIME);
    }

    public Object get(String key) {
        // key 必须使用同一个序列化
        Jedis jedis = getJedis();
        Object object;
        try {
            object = SerializableUtil.toObject(jedis.get(SerializableUtil.toBytes(key)));
        } finally {
            releaseJedis(jedis);
        }
        return object;
    }

    public List mget(List<String> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        // key 转为字节数组，兼容所有val
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
        System.out.println("cmd:redis key = " + key + ",seconds = " + seconds);
        if (seconds < 1) {
            // < 1 is  illegal parameter
            seconds = 1;
        }
        SetParams setParams = new SetParams();
        setParams.ex(seconds);
        Jedis jedis = getJedis();
        try {
            // key转化为字节，为了存储所有类型的value
            jedis.set(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value),setParams);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void replace(String key, Object value) {
        Jedis jedis = getJedis();
        try {
            // key转化为字节，为了存储所有类型的value
            jedis.set(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void append(String key, Object val) {
        Jedis jedis = getJedis();
        try {
            jedis.append(SerializableUtil.toBytes(key),SerializableUtil.toBytes(val));
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Object getAndTouch(String key, int seconds) {
        expire(key,seconds);
        return get(key);
    }

    public Object del(String key) {
        Object old = get(key);
        Jedis jedis = getJedis();
        try {
            jedis.del(SerializableUtil.toBytes(key));
        } finally {
            releaseJedis(jedis);
        }
        return old;
    }

    public void expire(String key, int seconds) {
        if (seconds < 1) {
            // < 1 is  illegal parameter
            seconds = 1;
        }
        Jedis jedis = getJedis();
        try {
            System.out.println("cmd:redis key = " + key + ",seconds = " + seconds);
            jedis.expire(SerializableUtil.toBytes(key),seconds);
        } finally {
            releaseJedis(jedis);
        }
    }

    public boolean contains(String key) {
        Jedis jedis = getJedis();
        Boolean exists;
        try {
            exists = jedis.exists(SerializableUtil.toBytes(key));
        } finally {
            releaseJedis(jedis);
        }
        return exists;
    }

    public void hSet(String cacheName, String key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.hset(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
        } finally {
            releaseJedis(jedis);
        }
    }

    public Object hGet(String cacheName, String key) {
        Jedis jedis = getJedis();
        Object object;
        try {
            object = SerializableUtil.toObject(jedis.hget(SerializableUtil.toBytes(cacheName), SerializableUtil.toBytes(key)));
        } finally {
            releaseJedis(jedis);
        }
        return object;
    }

    @Override
    public List hVals(String cacheName) {
        Jedis jedis = getJedis();
        Collection<byte[]> hvals;
        try {
            hvals = getJedis().hvals(SerializableUtil.toBytes(cacheName));
        } finally {
            releaseJedis(jedis);
        }
        if (hvals == null || hvals.size() == 0) {
            return  Collections.emptyList();
        }
        return SerializableUtil.toObjectList(hvals);
    }

    public boolean hContains(String cacheName, String key) {
        Jedis jedis = getJedis();
        Boolean hexists;
        try {
            hexists = jedis.hexists(SerializableUtil.toBytes(cacheName), SerializableUtil.toBytes(key));
        } finally {
            releaseJedis(jedis);
        }
        return hexists;
    }

    public Object hDel(String cacheName, String key) {
        Jedis jedis = getJedis();
        Object oldObject;
        try {
            oldObject = hGet(cacheName, key);
            jedis.hdel(SerializableUtil.toBytes(cacheName),SerializableUtil.toBytes(key));
        } finally {
            releaseJedis(jedis);
        }
        return oldObject;
    }

    @Override
    public void hDel(String cacheName) {
        Jedis jedis = getJedis();
        try {
            jedis.del(SerializableUtil.toBytes(cacheName));
        } finally {
            releaseJedis(jedis);
        }
    }

    public Set<String> hKeys(String cacheName) {
        Jedis jedis = getJedis();
        Set<byte[]> hkeys;
        try {
            hkeys = jedis.hkeys(SerializableUtil.toBytes(cacheName));
        } finally {
            releaseJedis(jedis);
        }
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
        Jedis jedis = getJedis();
        try {
            jedis.lpush(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Object lpop(String key) {
        Jedis jedis = getJedis();
        Object object;
        try {
            object = SerializableUtil.toObject(jedis.lpop(SerializableUtil.toBytes(key)));
        } finally {
            releaseJedis(jedis);
        }
        return object;
    }

    @Override
    public void rpush(String key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.rpush(SerializableUtil.toBytes(key),SerializableUtil.toBytes(value));
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Object rpop(String key) {
        Jedis jedis = getJedis();
        Object object;
        try {
            object = SerializableUtil.toObject(jedis.rpop(SerializableUtil.toBytes(key)));
        } finally {
            releaseJedis(jedis);
        }
        return object;
    }

}
