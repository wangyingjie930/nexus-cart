package com.alibaba.nexus.cart.application.impl;

import com.alibaba.nexus.cart.application.CartService;
import com.alibaba.nexus.cart.application.dto.CartView;
import com.alibaba.nexus.cart.application.dto.UserContext;
import com.alibaba.nexus.cart.domain.Cart;
import com.alibaba.nexus.cart.domain.CartItem;
import com.alibaba.nexus.cart.domain.repository.CartRepository;
import com.alibaba.nexus.cart.infrastructure.client.PromotionServiceClient;
import com.alibaba.nexus.cart.infrastructure.client.dto.DiscountApplication;
import com.alibaba.nexus.cart.infrastructure.client.dto.Fact;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final PromotionServiceClient promotionServiceClient;
    // private final ProductServiceClient productServiceClient; // 实际项目中会注入商品服务客户端

    public CartServiceImpl(CartRepository cartRepository, PromotionServiceClient promotionServiceClient) {
        this.cartRepository = cartRepository;
        this.promotionServiceClient = promotionServiceClient;
    }

    @Override
    @Transactional(readOnly = true)
    public CartView getCart(String userId, UserContext userContext) {
        Cart cart = findOrCreateCart(userId);

        // 实际项目中: 此处会调用商品服务，用最新的价格、库存、名称等信息更新 cart.getItems()

        // 调用促销服务计算优惠
        Fact fact = buildFact(cart, userContext);
        DiscountApplication discount = promotionServiceClient.calculateBestOffer(fact);

        return CartView.from(cart, discount);
    }

    @Override
    @Transactional
    public CartView addItemToCart(String userId, CartItem item) {
        Cart cart = findOrCreateCart(userId);
        cart.addItem(item);
        cart = cartRepository.save(cart);

        UserContext userContext = new UserContext(userId, false, null);
        Fact fact = buildFact(cart, userContext);
        DiscountApplication discount = promotionServiceClient.calculateBestOffer(fact);
        return CartView.from(cart, discount);
    }

    @Override
    @Transactional
    public CartView updateItemQuantity(String userId, String sku, int quantity) {
        Cart cart = findOrCreateCart(userId);
        cart.updateItemQuantity(sku, quantity);
        cart = cartRepository.save(cart);

        UserContext userContext = new UserContext(userId, false, null);
        Fact fact = buildFact(cart, userContext);
        DiscountApplication discount = promotionServiceClient.calculateBestOffer(fact);
        return CartView.from(cart, discount);
    }

    @Override
    @Transactional
    public void removeItemFromCart(String userId, String sku) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.removeItem(sku);
            cartRepository.save(cart);
        });
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);
    }

    private Fact buildFact(Cart cart, UserContext userContext) {
        Fact fact = new Fact();
        com.alibaba.nexus.cart.infrastructure.client.dto.UserContext promoUser = new com.alibaba.nexus.cart.infrastructure.client.dto.UserContext();
        promoUser.setId(Long.parseLong(userContext.getUserId()));
        promoUser.setVip(userContext.isVip());
        promoUser.setLabels(userContext.getLabels());

        fact.setUser(promoUser);
        fact.setItems(cart.getItems());

        // ========== 错误修复：调用正确的方法 ==========
        // 此处必须调用我们在 Cart.java 中定义的 calculateTotalAmount() 方法
        fact.setTotalAmount(cart.calculateTotalAmount());
        // ============================================

        fact.setEnvironment(new Fact.EnvironmentContext(Instant.now().toString(), "app"));
        return fact;
    }

    private Cart findOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId).orElse(new Cart(userId));
    }
}