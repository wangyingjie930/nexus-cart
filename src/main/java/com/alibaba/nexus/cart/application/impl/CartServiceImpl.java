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
    public CartView getCart(String userId, UserContext userContext) {
        Cart cart = findOrCreateCart(userId);

        // 实际项目中: 此处会调用商品服务，用最新的价格、库存、名称等信息更新 cart.getItems()

        // 调用促销服务计算优惠
        Fact fact = buildFact(cart, userContext);
        DiscountApplication discount = promotionServiceClient.calculateBestOffer(fact);

        return CartView.from(cart, discount);
    }

    @Override
    public CartView addItemToCart(String userId, CartItem item) {
        Cart cart = findOrCreateCart(userId);
        cart.addItem(item);
        cartRepository.save(cart);
        
        // 创建用户上下文（实际项目中应该从请求中获取）
        UserContext userContext = new UserContext(userId, false, null);
        return getCart(userId, userContext);
    }

    @Override
    public CartView updateItemQuantity(String userId, String sku, int quantity) {
        Cart cart = findOrCreateCart(userId);
        cart.updateItemQuantity(sku, quantity);
        cartRepository.save(cart);
        
        // 创建用户上下文（实际项目中应该从请求中获取）
        UserContext userContext = new UserContext(userId, false, null);
        return getCart(userId, userContext);
    }

    @Override
    public void removeItemFromCart(String userId, String sku) {
        Cart cart = findOrCreateCart(userId);
        cart.removeItem(sku);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    private Fact buildFact(Cart cart, UserContext userContext) {
        Fact fact = new Fact();
        com.alibaba.nexus.cart.infrastructure.client.dto.UserContext promoUser = new com.alibaba.nexus.cart.infrastructure.client.dto.UserContext();
        promoUser.setId(Long.parseLong(userContext.getUserId()));
        promoUser.setVip(userContext.isVip());
        promoUser.setLabels(userContext.getLabels());

        fact.setUser(promoUser);
        fact.setItems(cart.getItems());
        fact.setTotalAmount(cart.getTotalAmount());
        fact.setEnvironment(new Fact.EnvironmentContext(Instant.now().toString(), "app"));
        return fact;
    }

    private Cart findOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId).orElse(new Cart(userId));
    }
}