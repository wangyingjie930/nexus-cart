package com.alibaba.nexus.cart.infrastructure.repository;

import com.alibaba.nexus.cart.domain.Cart;
import com.alibaba.nexus.cart.domain.repository.CartRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisCartRepository implements CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_KEY_PREFIX = "cart:";
    private static final long CART_EXPIRATION_DAYS = 30;

    public RedisCartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        Cart cart = (Cart) redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);
        return Optional.ofNullable(cart);
    }

    @Override
    public void save(Cart cart) {
        redisTemplate.opsForValue().set(
                CART_KEY_PREFIX + cart.getUserId(),
                cart,
                CART_EXPIRATION_DAYS,
                TimeUnit.DAYS
        );
    }

    @Override
    public void deleteByUserId(String userId) {
        redisTemplate.delete(CART_KEY_PREFIX + userId);
    }
}