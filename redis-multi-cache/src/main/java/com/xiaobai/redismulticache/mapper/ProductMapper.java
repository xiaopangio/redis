package com.xiaobai.redismulticache.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaobai.redismulticache.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * className: ProductMapper
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:09
 * version: 1.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
