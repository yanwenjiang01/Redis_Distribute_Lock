package com.ict.common.redis;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : DevWenjiang
 * Description: Redis客户端类，与redis服务器进行数据交互类
 * @date : 2020/6/10-20:13
 */
@Component
public class RedisClient {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    /**
     * string字符串set方法
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * string字符串set方法,带有过期时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     */
    public void set(String key, String value, long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * Object对象set方法
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Object对象set方法,带有过期时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     */
    public void set(String key, Object value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * setNX方法
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setNX(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * setNX,带有过期时间
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    public Boolean setNX(String key, Object value, long expireTime, TimeUnit timeUnit) {
        return redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations redisOperations) throws DataAccessException {
                //开启事务
                redisOperations.multi();
                redisOperations.opsForValue().setIfAbsent(key, value);
                redisOperations.expire(key, expireTime, timeUnit);
                List exec = redisOperations.exec();
                if (exec == null || exec.isEmpty()) {
                    return false;
                }
                return (Boolean) exec.get(0);
            }
        });
    }

    /**
     *  自增
     * @param key
     * @param i
     * @return
     */
    public Long increment(String key, int i) {

        return stringRedisTemplate.boundValueOps(key).increment(i);
    }

    /**
     * 获取key对应值
     * @param key
     * @return
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * Object的
     * @param key
     * @return
     */
    public Object getObj(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 模糊查询满足要求的key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * 获得过期时间（以秒为单位）
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key,long timeout,TimeUnit unit){
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 删除key
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     * @param keys
     */
    public void delete(Set<String> keys){
        redisTemplate.delete(keys);
    }

    /**
     * 对key进行hash
     * @param key
     * @return
     */
    public boolean haskey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 右进
     * @param key
     * @param value
     * @return
     */
    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 左出
     * @param key
     * @return
     */
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 获取list集合
     * @param key
     * @return
     */
    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }



}
