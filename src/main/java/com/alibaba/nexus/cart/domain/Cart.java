package com.alibaba.nexus.cart.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@Entity
@Table(name = "carts")
@EntityListeners(AuditingEntityListener.class) // 启用JPA审计功能（自动填充创建/更新时间）
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 使用无业务含义的自增ID作为主键

    @Column(nullable = false, unique = true)
    private String userId;

    // 关键：定义一对多关系
    // 1. mappedBy = "cart": 指定关系由 CartItem 实体的 "cart" 字段维护
    // 2. cascade = CascadeType.ALL: 级联操作，对 Cart 的所有操作（增删改）都会应用到关联的 CartItems
    // 3. orphanRemoval = true: 当一个 CartItem 从 items 集合中移除时，该 CartItem 也会从数据库中删除
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    // @Transient 注解表示该字段不映射到数据库表，它是动态计算出来的
    @Transient
    private long totalAmount = 0L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Cart(String userId) {
        this.userId = userId;
    }

    // REMOVED: The @PostLoad calculation logic is no longer needed in the entity.
    // @PostLoad
    // public void calculateTotalAmount() { ... }

    // --- Business logic methods remain unchanged ---

    public void addItem(CartItem newItem) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getSku().equals(newItem.getSku()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
        } else {
            // 关键：建立双向关联
            newItem.setCart(this);
            items.add(newItem);
        }
    }

    public void removeItem(String sku) {
        items.removeIf(item -> item.getSku().equals(sku));
    }

    public void updateItemQuantity(String sku, int quantity) {
        if (quantity <= 0) {
            removeItem(sku);
            return;
        }
        items.stream()
                .filter(item -> item.getSku().equals(sku))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    /**
     * A new helper method to calculate the total amount on demand.
     * This makes the calculation explicit and callable from the service layer.
     * @return The total amount of all items in the cart.
     */
    public long calculateTotalAmount() {
        return this.items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}