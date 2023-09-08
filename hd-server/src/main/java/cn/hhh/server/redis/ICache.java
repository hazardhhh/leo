package cn.hhh.server.redis;

import java.util.List;
import java.util.Set;

/**
 * @Description ICache
 * @Author HHH
 * @Date 2023/9/9 2:29
 */
public interface ICache {

    void set(String key, Object value);

    void set(String key, Object value, int seconds);

    void replace(String key, Object value, int seconds);

    void replace(String key, Object value);

    void append(String key, Object val);

    Object get(String key);

    Object getAndTouch(String key, int seconds);
    List mget(List<String> keys);

    Object del(String key);

    void expire(String key, int seconds);

    boolean contains(String key);

    void hSet(String cacheName, String key, Object value);

    Object hGet(String cacheName, String key);

    List hVals(String cacheName);

    boolean hContains(String cacheName, String key);

    Object hDel(String cacheName, String key);

    void hDel(String cacheName);

    Set<String> hKeys(String cacheName);

    void lpush(String key, Object value);

    Object lpop(String key);

    void rpush(String key, Object value);

    Object rpop(String key);

}
