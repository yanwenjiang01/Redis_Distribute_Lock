package com.ict.common.aop;

import com.ict.common.annotion.RedisDistributeLockAnnotion;
import com.ict.common.exception.RedisDistributeLockException;
import com.ict.common.lock.RedisClient;
import com.ict.common.lock.RedisDistributeLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * @author: DevWenjiang
 * Description: Redis分布式锁切面类，加入注解开启分布式锁
 * @date : 2020-06-11 09:36
 */
@Component
@Aspect
public class RedisDistributeLockAspect {
    //日志对象
    private static final Logger log = LoggerFactory.getLogger(RedisDistributeLockAspect.class);

    //注入redisClient
    @Autowired
    private RedisClient redisClient;


    //存放重试次数的ThreadLocal
    private static ThreadLocal<Integer> retryTimes = new ThreadLocal<>();

    //注解切面，只要方法上加该注解就会进行切面方法增强
    @Pointcut("@annotation(com.ict.common.annotion.RedisDistributeLockAnnotion)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object aroundMethod(ProceedingJoinPoint pj) throws RedisDistributeLockException {
        //获取方法参数
        Object[] args = pj.getArgs();
        //获取方法签名,方法名称
        MethodSignature signature = (MethodSignature) pj.getSignature();
        //获取参数名
        String[] parameterNames = signature.getParameterNames();
        //存放参数名：参数值
        HashMap<String, Object> params = new HashMap<>();
        if (null != parameterNames && parameterNames.length > 0 && null != args && args.length > 0) {
            for (int i = 0; i < parameterNames.length; i++) {
                params.put(parameterNames[i], args[i]);
            }
        }
        //MethodSignature signature = (MethodSignature) pj.getSignature();
        //获取方法注解
        RedisDistributeLockAnnotion redisDistributeLockAnnotion = signature.getMethod().getAnnotation(RedisDistributeLockAnnotion.class);
        //获取key
        String key = redisDistributeLockAnnotion.key();
        if (null == key || key.trim().equals("")) {
            key = redisDistributeLockAnnotion.value();
        }
        if (null == key || key.trim().equals("")) {

            log.error("key值不能为空");
            throw new RedisDistributeLockException("key值不能为空");
        }
        //创建redis分布式锁对象
        RedisDistributeLock redisDistributeLock = RedisDistributeLock.build(redisClient, key, redisDistributeLockAnnotion.maxLockTime());
        try {
            Boolean lock = redisDistributeLock.lock();
            if (lock){
                try {
                    //获取锁成功,执行方法
                    System.out.println(Thread.currentThread().getName()+"获取到锁"+key);
                    Object proceed = pj.proceed(args);
                    //移除ThreadLocal中的重试次数
                    retryTimes.remove();
                    //返回执行结果
                    return proceed;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }else {
                System.out.println(Thread.currentThread().getName()+"没有获取到锁"+key);
            }

            switch (redisDistributeLockAnnotion.LOCK_STRATEGY()){
                case IGNORE:
                    break;
                case THROWABLE:
                    throw new RedisDistributeLockException("该"+key+"已经被获取");
                case WAIT_RETRY:
                    //获取
                    Integer retryTime = retryTimes.get();
                    if (retryTime != null &&retryTime >= redisDistributeLockAnnotion.maxRetryTimes()){
                        retryTimes.remove();
                        throw new RedisDistributeLockException(Thread.currentThread().getId()+"尝试获取锁失败超过了最大重试次数,key="+key);
                    }
                    if (retryTime == null){
                        retryTime = 1;
                    }else {
                        retryTime++;
                    }
                    retryTimes.set(retryTime);
                    try{
                        Thread.sleep(redisDistributeLockAnnotion.retryTime());
                    }catch (Exception e){
                        log.error(e.getMessage(),e);
                    }
                    aroundMethod(pj);
                    break;
                default:break;
            }
            return null;
        }finally {
            redisDistributeLock.unlock();
        }
    }

}
