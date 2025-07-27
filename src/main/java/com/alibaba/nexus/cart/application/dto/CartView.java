package com.alibaba.nexus.cart.application.dto;

import com.alibaba.nexus.cart.domain.Cart;
import com.alibaba.nexus.cart.infrastructure.client.dto.DiscountApplication;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartView {
    private String userId;
    private List<CartItemDto> items;
    private long totalAmount; // 总金额（单位：分）
    private long discountAmount; // 优惠金额（单位：分）
    private long finalAmount; // 最终金额（单位：分）
    private String discountDescription; // 优惠描述

    public static CartView from(Cart cart, DiscountApplication discount) {
        CartView view = new CartView();
        view.setUserId(cart.getUserId());
        view.setItems(cart.getItems().stream()
                .map(CartItemDto::from)
                .collect(Collectors.toList()));
        view.setTotalAmount(cart.getTotalAmount());
        view.setDiscountAmount(discount.getDiscountAmount());
        view.setFinalAmount(cart.getTotalAmount() - discount.getDiscountAmount());
        view.setDiscountDescription(discount.getDescription());
        return view;
    }
}
