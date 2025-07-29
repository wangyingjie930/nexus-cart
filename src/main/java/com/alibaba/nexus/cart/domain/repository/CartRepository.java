package com.alibaba.nexus.cart.domain.repository;

import com.alibaba.nexus.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 1. 继承 JpaRepository<实体类型, 主键类型>
// 2. Spring Data JPA 会自动为我们实现所有基础的 CRUD 方法
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Spring Data JPA 的强大之处：根据方法名自动生成查询
    // 这等同于 "SELECT c FROM Cart c WHERE c.userId = :userId"
    Optional<Cart> findByUserId(String userId);

    // save, deleteById 等方法已由 JpaRepository 提供，无需再次定义
}