package com.pdmxz.gmall.common.cache;

import com.pdmxz.gmall.common.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Around(value = "@annotation(com.pdmxz.gmall.common.cache.GmallCache)")
    public Object cacheData(ProceedingJoinPoint pjp) {

        //获取方法签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        String prefix = annotation.prefix();

        //分布式锁的key
        String cacheLock = prefix + Arrays.asList(pjp.getArgs()) + RedisConst.SKULOCK_SUFFIX;
        //缓存Key
        String cacheKey = prefix + Arrays.asList(pjp.getArgs()) + RedisConst.SKUKEY_SUFFIX;

        Object o = redisTemplate.opsForValue().get(cacheKey);
        if (o == null) {
            //说明redis没有缓存,准备上分布式锁。防止缓存击穿
            RLock lock = redissonClient.getLock(cacheLock);
            try {
                boolean isLock = lock.tryLock(1, 2, TimeUnit.SECONDS);

                if (isLock) {
                    try {
                        //获取到锁
                        o = pjp.proceed();
                        if (o != null) {
                            //防止缓存雪崩
                            redisTemplate.opsForValue().set(cacheKey, o, RedisConst.SKUKEY_TIMEOUT
                                    + new Random().nextInt(500), TimeUnit.SECONDS);
                        } else {
                            //防止缓存穿透
                            o = method.getReturnType().newInstance();
                            redisTemplate.opsForValue().set(cacheKey, o, 5, TimeUnit.MINUTES);
                        }
                    }finally {
                        //释放锁
                        lock.unlock();
                    }
                } else {
                    //没有获取到锁
                    TimeUnit.SECONDS.sleep(2);
                    o = redisTemplate.opsForValue().get(cacheKey);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return o;
    }

}
