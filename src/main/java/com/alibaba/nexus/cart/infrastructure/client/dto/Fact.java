package com.alibaba.nexus.cart.infrastructure.client.dto;

import com.alibaba.nexus.cart.domain.CartItem;
import lombok.Data;

import java.util.List;

@Data
public class Fact {
    private UserContext user;
    private List<CartItem> items;
    private long totalAmount;
    private EnvironmentContext environment;

    @Data
    public static class EnvironmentContext {
        private String timestamp;
        private String source;

        public EnvironmentContext(String timestamp, String source) {
            this.timestamp = timestamp;
            this.source = source;
        }
    }
}
