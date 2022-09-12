package com.xiaobai.redismulticache.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * className: Product
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:09
 * version: 1.0
 */
@Data
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String price;
    private Integer stock;
}
