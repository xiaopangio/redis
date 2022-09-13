package com.xiaobai.redismulticache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class RedisMultiCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisMultiCacheApplication.class, args);
    }

}
