package com.alibaba.nexus.cart.application;

import com.alibaba.nexus.cart.application.dto.CartView;
import com.alibaba.nexus.cart.application.dto.UserContext;
import com.alibaba.nexus.cart.domain.CartItem;

public interface CartService {
    CartView getCart(String userId, UserContext userContext);
    CartView addItemToCart(String userId, CartItem item);
    CartView updateItemQuantity(String userId, String sku, int quantity);
    void removeItemFromCart(String userId, String sku);
    void clearCart(String userId);
}