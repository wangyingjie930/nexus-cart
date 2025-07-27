package com.alibaba.nexus.cart.infrastructure.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserContext {
    private Long id;
    private boolean vip;
    private List<String> labels;
} 