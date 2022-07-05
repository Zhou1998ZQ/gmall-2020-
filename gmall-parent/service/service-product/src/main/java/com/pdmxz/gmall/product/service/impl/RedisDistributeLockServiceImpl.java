package com.pdmxz.gmall.product.service.impl;

import com.pdmxz.gmall.product.service.RedisDistributeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisDistributeLockServiceImpl implements RedisDistributeLockService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void testLock()  {
        String uuid = UUID.randomUUID().toString();
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,2,TimeUnit.SECONDS);
        //获取锁
        if (isLock){
            Integer num = (Integer) redisTemplate.opsForValue().get("num");
            num++;
            redisTemplate.opsForValue().set("num",num);
            //释放锁
//            if (uuid.equals(redisTemplate.opsForValue().get("lock"))){
//                redisTemplate.delete("lock");
//            }
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return tostring(redis.call('del',KEYS[1])) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript<>(script), Collections.singletonList("lock"), uuid);

        }else {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testLock();
        }
    }
}
