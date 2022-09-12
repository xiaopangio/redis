package com.xiaobai.redismulticache.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobai.redismulticache.common.JSONUtil;
import com.xiaobai.redismulticache.common.RedisUtil;
import com.xiaobai.redismulticache.mapper.ProductMapper;
import com.xiaobai.redismulticache.pojo.Product;
import com.xiaobai.redismulticache.service.ProductService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * className: ProductServiceImpl
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:11
 * version: 1.0
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    private static final String PRODUCT_PREFIX = "product:";
    private static final String PRODUCT_UPDATE_PREFIX = "product:update:";
    private static final String PRODUCT_HOT_KEY_CREATE_PREFIX = "product:hot:key:create:";
    private static final String EMPRY_CACHE="{}";
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private Redisson redisson;

    @Override
    public Product get(Integer id) throws InterruptedException {
        String productCacheKey=PRODUCT_PREFIX+id;
        Product product = null;
        product= getProductFromCache(productCacheKey);
        if(product!=null){
            return product;
        }
//        解决缓存击穿问题，通过分布式锁，进行热点数据重建，
        RLock hotKeyLock = redisson.getLock(PRODUCT_HOT_KEY_CREATE_PREFIX + id);
//        hotKeyLock.lock();
//        假如2万的并发，一个线程会阻塞其他所有的线程来重建缓存，缓存构建完后，后面的19999的线程依旧会进行加锁，取缓存，解锁。
//        但是这个时候缓存已经重建了，不需要再加锁了。所以我们选择了tryLock，这个方法会在一定时间内尝试加锁，超过时间后就不会再加锁了，就会直接执行后面的代码
//        加不了锁的就正好符合我们的期望，因为我们是不希望后面的19999个线程来加锁的
//        经过如此的优化，超过加锁时间的其他线程，就会并发去执行
//        但是这个优化需要建立在第一个线程能在规定的时间内成功构建了缓存，否则过了这个时间，其他线程就不会再去等待锁，直接去查缓存，但是缓存还没有构建好，就会出现缓存击穿的问题
        hotKeyLock.tryLock(2,TimeUnit.SECONDS);
        try{
            product=getProductFromCache(productCacheKey);
            if (product!=null){
                return product;
            }
            //解决双写不一致问题，通过加读锁，读锁不会阻塞读锁，但是会阻塞写锁
            RReadWriteLock RWL = redisson.getReadWriteLock(PRODUCT_UPDATE_PREFIX + id);
            RLock readLock = RWL.readLock();
            readLock.lock();
            try {
                product=productMapper.selectById(id);
                if(product!=null){
                    RedisUtil.set(productCacheKey, JSONUtil.toJSONString(product),getProductCacheTimeout(), TimeUnit.SECONDS);
                }else {
//                    防止缓存穿透，将空对象放入缓存
                    RedisUtil.set(productCacheKey,EMPRY_CACHE,getEmptyCacheTimeout(),TimeUnit.SECONDS);
                }
            } finally {
                readLock.unlock();
            }
        }finally {
            hotKeyLock.unlock();
        }
        return product;
    }

    private int getEmptyCacheTimeout() {
        return new Random().nextInt(5)*60;
    }

    private Product getProductFromCache(String productCacheKey){
        Product product = new Product();
        String productStr = RedisUtil.get(productCacheKey);
        if(StringUtils.hasText(productStr)){
            if(EMPRY_CACHE.equals(productCacheKey)){
                RedisUtil.set(productCacheKey,EMPRY_CACHE,getEmptyCacheTimeout(),TimeUnit.SECONDS);
//                为了区别数据库中没有数据和缓存中没有数据，当缓存中存在我们为解决缓存穿透而设置的空对象时，返回一个实例化的空对象，前端可以根据这个对象的id是否为null来判断是否有数据
                return new Product();
            }
            //可以有效的对于热点数据进行延期，防止缓存过期
            product=  JSONUtil.parseObject(productStr,Product.class);
            RedisUtil.set(productCacheKey,productStr,getProductCacheTimeout(),TimeUnit.SECONDS);
        }
        return product;
    }
    @Transactional
    @Override
    public Product create(Product product) {
        productMapper.insert(product);
        RedisUtil.set(PRODUCT_PREFIX + product.getId(), JSONUtil.toJSONString(product), getProductCacheTimeout(), TimeUnit.SECONDS);
        return product;
    }

    private int getProductCacheTimeout() {
        return new Random().nextInt(5) * 60 * 60 + 24 * 60 * 60;
    }

    @Transactional
    @Override
    public Product update(Product product) {
        Product productResult = new Product();
//        为了防止缓存和数据库不一致，先加写锁，阻塞读锁和写锁，然后更新数据库，再更新缓存
        RReadWriteLock RWL = redisson.getReadWriteLock(PRODUCT_UPDATE_PREFIX + product.getId());
        RLock writeLock = RWL.writeLock();
        writeLock.lock();
        try {
            productMapper.updateById(product);
            RedisUtil.set(PRODUCT_PREFIX + product.getId(), JSONUtil.toJSONString(product), getProductCacheTimeout(), TimeUnit.SECONDS);
        } finally {
            writeLock.unlock();
        }
        return productResult;
    }
}
