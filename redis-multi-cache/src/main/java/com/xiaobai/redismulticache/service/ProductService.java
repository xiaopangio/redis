package com.xiaobai.redismulticache.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaobai.redismulticache.pojo.Product;

public interface ProductService extends IService<Product>  {
    Product get(Integer id);
    Product create(Product product);
    Product update(Product product);
}
