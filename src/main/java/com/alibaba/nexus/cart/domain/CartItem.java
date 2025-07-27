package com.alibaba.nexus.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sku;
    private long price;      // 商品单价（单位：分）
    private int quantity;
    private String category; // 商品品类
    private String brand;    // 商品品牌
    private String name;     // 商品名称 (从商品服务获取)
    private String imageUrl; // 商品图片 (从商品服务获取)
}