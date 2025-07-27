package com.alibaba.nexus.cart.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private long totalAmount = 0L; // 总金额（单位：分）

    public Cart(String userId) {
        this.userId = userId;
    }

    public void addItem(CartItem newItem) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getSku().equals(newItem.getSku()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
        recalculateTotalAmount();
    }

    public void removeItem(String sku) {
        items.removeIf(item -> item.getSku().equals(sku));
        recalculateTotalAmount();
    }

    public void updateItemQuantity(String sku, int quantity) {
        if (quantity <= 0) {
            removeItem(sku);
            return;
        }
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getSku().equals(sku))
                .findFirst();
        existingItem.ifPresent(item -> item.setQuantity(quantity));
        recalculateTotalAmount();
    }

    public void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}