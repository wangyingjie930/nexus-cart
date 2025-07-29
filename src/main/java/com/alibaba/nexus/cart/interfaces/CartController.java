package com.alibaba.nexus.cart.interfaces;

import com.alibaba.nexus.cart.application.CartService;
import com.alibaba.nexus.cart.application.dto.CartView;
import com.alibaba.nexus.cart.application.dto.UserContext;
import com.alibaba.nexus.cart.domain.CartItem;
import com.alibaba.nexus.cart.interfaces.dto.AddItemRequest;
import com.alibaba.nexus.cart.interfaces.dto.UpdateQuantityRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartView> getCart(@PathVariable String userId, UserContext userContext) {
        return ResponseEntity.ok(cartService.getCart(userId, userContext));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartView> addItem(@PathVariable String userId, @RequestBody AddItemRequest request) {
        // ========== 错误修复：修改对象创建方式 ==========
        // 1. 使用无参构造函数创建一个空的 CartItem 对象
        CartItem item = new CartItem();

        // 2. 使用 setter 方法从请求 DTO 中填充数据
        // 这种方式不依赖于构造函数的参数顺序和数量，更加健壮
        item.setSku(request.getSku());
        item.setPrice(request.getPrice());
        item.setQuantity(request.getQuantity());
        item.setCategory(request.getCategory());
        item.setBrand(request.getBrand());
        // name 和 imageUrl 字段将由后端服务（如商品服务）填充，前端添加时无需传入
        // ============================================
        return ResponseEntity.ok(cartService.addItemToCart(userId, item));
    }

    @PutMapping("/{userId}/items/{sku}")
    public ResponseEntity<CartView> updateQuantity(@PathVariable String userId, @PathVariable String sku, @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, sku, request.getQuantity()));
    }

    @DeleteMapping("/{userId}/items/{sku}")
    public ResponseEntity<Void> removeItem(@PathVariable String userId, @PathVariable String sku) {
        cartService.removeItemFromCart(userId, sku);
        return ResponseEntity.noContent().build();
    }
}