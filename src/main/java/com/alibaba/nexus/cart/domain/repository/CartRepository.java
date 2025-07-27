package com.alibaba.nexus.cart.domain.repository;

import com.alibaba.nexus.cart.domain.Cart;
import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);
    void save(Cart cart);
    void deleteByUserId(String userId);
}