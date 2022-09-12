package com.xiaobai.redismulticache.common;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * className: RedisUtil
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:01
 * version: 1.0
 */
@Component
public class RedisUtil implements ApplicationContextAware {
    public static StringRedisTemplate stringRedisTemplate;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    }
    public static String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }
    public static void set(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }
    public static void set(String key, String value, int time, TimeUnit timeUnit){
        stringRedisTemplate.opsForValue().set(key,value,time,timeUnit);
    }
}
