package com.ict.common.exception;

/**
 * @author : DevWenjiang
 * Description: redis分布式锁异常类
 * @date : 2020/6/10-20:10
 */
public class RedisDistributeLockException extends Exception {
    public RedisDistributeLockException() {
    }

    public RedisDistributeLockException(String message) {
        super(message);
    }

    public RedisDistributeLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisDistributeLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
