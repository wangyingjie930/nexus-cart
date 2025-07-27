package com.alibaba.nexus.cart.application.dto;

import com.alibaba.nexus.cart.domain.CartItem;
import lombok.Data;

@Data
public class CartItemDto {
    private String sku;
    private long price;      // 商品单价（单位：分）
    private int quantity;
    private String category; // 商品品类
    private String brand;    // 商品品牌
    private String name;     // 商品名称
    private String imageUrl; // 商品图片
    private long totalPrice; // 商品总价（单位：分）

    public static CartItemDto from(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setSku(item.getSku());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setCategory(item.getCategory());
        dto.setBrand(item.getBrand());
        dto.setName(item.getName());
        dto.setImageUrl(item.getImageUrl());
        dto.setTotalPrice(item.getPrice() * item.getQuantity());
        return dto;
    }
}
