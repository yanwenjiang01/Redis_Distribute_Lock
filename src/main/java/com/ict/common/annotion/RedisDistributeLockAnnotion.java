package com.ict.common.annotion;

import com.ict.common.enums.LockStrategy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: DevWenjiang
 * Description: redis分布式锁注解(需要加锁的方法加入注解)
 * @date : 2020-06-10 17:23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisDistributeLockAnnotion {
    @AliasFor("value")
    String key() default "";
    @AliasFor("key")
    String value() default "";

    //锁策略：失败忽略|重试|抛出异常
    LockStrategy LOCK_STRATEGY() default LockStrategy.IGNORE;
    /**
     * 获锁最长时间
     */
    long maxLockTime() default 30000l;

    /**
     * 等待重试时间
     */
    long retryTime() default 500l;

    /**
     * 最大重试次数
     */
    int maxRetryTimes() default 10;

}
