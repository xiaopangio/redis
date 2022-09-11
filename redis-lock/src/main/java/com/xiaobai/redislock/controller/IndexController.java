package com.xiaobai.redislock.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * className: IndexController
 * description:
 * author: xiaopangio
 * date: 2022/9/11 14:18
 * version: 1.0
 */
@RestController
public class IndexController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @RequestMapping("/deduct_stock")
    public String deductStock(){
        String lockKey = "product_001";
        String clientId = UUID.randomUUID().toString();
        // 加锁
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 10, TimeUnit.SECONDS);
        if(!result){
            return "error";
        }
        try{
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if(stock>0){
                int realStock=stock-1;
                stringRedisTemplate.opsForValue().set("stock",realStock+"");
                System.out.println("扣减成功，剩余库存："+realStock);
            }else {
                System.out.println("扣减失败，库存不足");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                // 释放锁
                stringRedisTemplate.delete(lockKey);
            }
        }
        return "end";
    }

}
