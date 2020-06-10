package com.ict.common.redis;


/**
 * @author : DevWenjiang
 * Description: redis分布式锁实现类
 * @date : 2020/6/10-20:38
 */
public class RedisDistributeLock {
    //默认最大线程获取锁时间
    private static final long DEFAULT_MAX_LOCK_TIME = 300000l;//30s

    //redis客户端
    private RedisClient redisClient;

    //key
    private String key;

    //最大锁时间
    private long maxLockTime;

    //持有锁状态
    private Boolean isLock;



}
