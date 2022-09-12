package com.xiaobai.redismulticache.controller;

import com.xiaobai.redismulticache.pojo.Product;
import com.xiaobai.redismulticache.pojo.Result;
import com.xiaobai.redismulticache.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * className: ProductController
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:06
 * version: 1.0
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/get/{productId}")
    public Result get(@PathVariable("productId") int productId){
        Product product = productService.getById(productId);
        return new Result<Product>(200,product);
    }
}
