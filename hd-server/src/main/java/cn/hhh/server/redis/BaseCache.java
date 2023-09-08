package cn.hhh.server.redis;

import cn.hhh.server.logger.HdLog;

import java.util.List;
import java.util.Set;

/**
 * @Description 缓存实现抽象类
 * @Author HHH
 * @Date 2023/9/9 2:38
 */
public class BaseCache implements ICache {

    protected static final HdLog log = HdLog.getInstance();

    @Override
    public void set(String key, Object value) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override set method");
    }

    @Override
    public void set(String key, Object value, int seconds) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override replace method");
    }

    @Override
    public void replace(String key, Object value, int seconds) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override replace method");
    }

    @Override
    public void replace(String key, Object value) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override replace method");
    }

    @Override
    public void append(String key, Object val) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override append method");
    }

    @Override
    public Object get(String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override get method");
    }

    @Override
    public Object getAndTouch(String key, int seconds) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override getAndTouch method");
    }

    @Override
    public List mget(List<String> keys) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override mget method");
    }

    @Override
    public Object del(String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override del method");
    }

    @Override
    public void expire(String key, int seconds) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override expire method");

    }

    @Override
    public boolean contains(String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override contains method");
    }

    @Override
    public void hSet(String cacheName, String key, Object value) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hSet method");

    }

    @Override
    public Object hGet(String cacheName, String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hGet method");
    }

    @Override
    public List hVals(String cacheName) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hVals method");
    }

    @Override
    public boolean hContains(String cacheName, String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hContains method");
    }

    @Override
    public Object hDel(String cacheName, String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hDel method");
    }

    @Override
    public void hDel(String cacheName) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hDel method");
    }

    @Override
    public Set<String> hKeys(String cacheName) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override hKeys method");
    }

    @Override
    public void lpush(String key, Object value) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override lpush method");
    }

    @Override
    public Object lpop(String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override lpop method");
    }

    @Override
    public void rpush(String key, Object value) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override rpush method");
    }

    @Override
    public Object rpop(String key) {
        throw new IllegalArgumentException(this.getClass().getName() + "no Override rpop method");
    }

}
