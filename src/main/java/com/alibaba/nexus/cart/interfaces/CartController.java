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
    public ResponseEntity<CartView> getCart(@PathVariable String userId,
                                            @RequestHeader(value = "X-User-Vip", defaultValue = "false") boolean isVip,
                                            @RequestHeader(value = "X-User-Labels", required = false) String labels) {
        UserContext userContext = new UserContext(userId, isVip, labels == null ? Collections.emptyList() : Collections.singletonList(labels));
        return ResponseEntity.ok(cartService.getCart(userId, userContext));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartView> addItem(@PathVariable String userId, @RequestBody AddItemRequest request) {
        CartItem item = new CartItem(request.getSku(), request.getPrice(), request.getQuantity(), request.getCategory(), request.getBrand(), null, null);
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