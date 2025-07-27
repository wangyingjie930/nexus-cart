package com.alibaba.nexus.cart.infrastructure.client;

import com.alibaba.nexus.cart.infrastructure.client.dto.DiscountApplication;
import com.alibaba.nexus.cart.infrastructure.client.dto.Fact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PromotionServiceClient {

    private final RestTemplate restTemplate;
    private final String promotionServiceUrl;

    public PromotionServiceClient(RestTemplate restTemplate, @Value("${services.promotion.url}") String url) {
        this.restTemplate = restTemplate;
        this.promotionServiceUrl = url + "/offers/calculate-best";
    }

    public DiscountApplication calculateBestOffer(Fact fact) {
        try {
            return restTemplate.postForObject(promotionServiceUrl, fact, DiscountApplication.class);
        } catch (Exception e) {
            // 在实际生产中，这里应该有更完善的日志记录和容错（如Hystrix或Resilience4J）
            System.err.println("Error calling promotion service: " + e.getMessage());
            // 返回一个无优惠的默认值，保证主流程不中断
            return new DiscountApplication(0L, "Error", "促销服务暂不可用");
        }
    }
}