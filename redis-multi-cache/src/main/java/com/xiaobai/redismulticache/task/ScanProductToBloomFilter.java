package com.xiaobai.redismulticache.task;

import com.xiaobai.redismulticache.filter.BloomFilter;
import com.xiaobai.redismulticache.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * className: ScanProductToBloomFilter
 * description:
 * author: xiaopangio
 * date: 2022/9/13 10:21
 * version: 1.0
 */
@Component
@Slf4j
public class ScanProductToBloomFilter {
    @Autowired
    private ProductService productService;
    @Autowired
    private BloomFilter bloomFilter;
    @Scheduled(cron = "0 * * * * ?")
    public void scanProductToBloomFilter(){
        productService.list().forEach(product->bloomFilter.add(product.getId()+""));
        log.info("布隆过滤器初始化完成");
    }
}
