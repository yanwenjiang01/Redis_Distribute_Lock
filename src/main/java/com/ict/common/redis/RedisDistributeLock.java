package com.ict.common.redis;


import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author : DevWenjiang
 * Description: redis分布式锁实现类
 * @date : 2020/6/10-20:38
 */
public class RedisDistributeLock {
    //默认最大线程获取锁时间
    private static final Long DEFAULT_MAX_LOCK_TIME = 300000l;//30s

    //redis客户端
    private RedisClient redisClient;

    //key
    private String key;

    //最大锁时间
    private Long maxLockTime;

    //持有锁状态
    private Boolean isLock;


    private RedisDistributeLock(RedisClient redisClient, String key, Long maxLockTime) {
        Assert.isNull(redisClient, "redisClient不能为空");
        Assert.hasText(key, "key不能为空");
        this.redisClient = redisClient;
        this.key = key;
        this.maxLockTime = maxLockTime;
    }

    /**
     * 建造者模式
     * @param redisClient
     * @param key
     * @param maxLockTime
     * @return
     */
    public static RedisDistributeLock build(RedisClient redisClient,String key,Long maxLockTime){
        return new RedisDistributeLock(redisClient,key,maxLockTime);
    }

    public static RedisDistributeLock build(RedisClient redisClient,String key){
        return new RedisDistributeLock(redisClient,key,DEFAULT_MAX_LOCK_TIME);
    }

    /**
     * 获取锁
     * @return
     */
    public Boolean lock(){
        Boolean aBoolean = redisClient.setNX(key, "1", maxLockTime, TimeUnit.MILLISECONDS);
        if (aBoolean){
            isLock = true;
            return true;
        }else {
            isLock = false;
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(){
        if (isLock){
            redisClient.delete(key);
            isLock = false;
        }
    }

}
