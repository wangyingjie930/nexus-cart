package com.alibaba.nexus.cart.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代表当前用户信息的上下文，用于在服务内部传递。
 * 这个信息通常从API网关的JWT或Session中解析得到。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private String userId;
    private boolean isVip;
    private List<String> labels; // e.g., "new_user", "high_value"
}