package com.alibaba.nexus.cart.infrastructure.config;

import com.alibaba.nexus.cart.interfaces.resolver.UserContextArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserContextArgumentResolver userContextArgumentResolver;

    public WebMvcConfig(UserContextArgumentResolver userContextArgumentResolver) {
        this.userContextArgumentResolver = userContextArgumentResolver;
    }

    /**
     * 我来注册
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 将我们的 UserContext 解析器注册到 Spring MVC
        resolvers.add(userContextArgumentResolver);
    }
}
