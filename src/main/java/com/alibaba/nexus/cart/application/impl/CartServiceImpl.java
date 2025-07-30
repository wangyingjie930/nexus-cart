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
import io.seata.spring.annotation.GlobalTransactional;

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

    /**
     * 添加商品到购物车
     * @GlobalTransactional: 开启一个全局事务。
     * name: 给这个全局事务起一个名字，便于追踪和识别。
     * rollbackFor: 指定什么异常会触发回滚，Exception.class 表示任何异常都会触发。
     */
    @Override
    @GlobalTransactional(name = "cart-add-item-tx", rollbackFor = Exception.class)
    @Transactional // 本地事务注解依然需要，保证本地数据库操作的原子性
    public CartView addItemToCart(String userId, CartItem item) {
        // 1. 本地数据库操作 (RM-1)
        Cart cart = findOrCreateCart(userId);
        cart.addItem(item);
        cart = cartRepository.save(cart);

        // 假设 UserContext 是临时构建的
        UserContext userContext = new UserContext(userId, false, null);

        // 2. 远程 RPC 调用促销服务 (RM-2)
        Fact fact = buildFact(cart, userContext);
        DiscountApplication discount = promotionServiceClient.calculateBestOffer(fact);

        // 如果 promotionServiceClient.calculateBestOffer 内部出现异常，
        // 或者这个方法后续的代码出现异常，Seata 会通知 cartRepository 回滚刚才的 save 操作。

        return CartView.from(cart, discount);
    }

    /**
     * 更新商品数量，同样需要全局事务保护
     */
    @Override
    @GlobalTransactional(name = "cart-update-quantity-tx", rollbackFor = Exception.class)
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