package com.xiaobai.redislock.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * className: RedissonConfig
 * description:
 * author: xiaopangio
 * date: 2022/9/11 17:20
 * version: 1.0
 */
@Configuration
public class RedissonConfig {
    @Bean
    public Redisson redisson(){
        Config config=new Config();
        config.useSingleServer().setAddress("redis://192.168.229.129:6379").setPassword("root").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
