package com.alibaba.nexus.cart.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountApplication {
    private long discountAmount; // 优惠金额（单位：分）
    private String code;         // 优惠码
    private String description;  // 优惠描述
}
