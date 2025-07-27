package com.alibaba.nexus.cart.interfaces.dto;

import lombok.Data;

@Data
public class AddItemRequest {
    private String sku;
    private long price;      // 商品单价（单位：分）
    private int quantity;
    private String category; // 商品品类
    private String brand;    // 商品品牌
}
