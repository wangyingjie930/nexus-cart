package com.alibaba.nexus.cart.interfaces.resolver;

import com.alibaba.nexus.cart.application.dto.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 我能处理！遇到UserContext, 就交给我
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getGenericParameterType().equals(UserContext.class);
    }

    /**
     * 我来创建！返回填充好数据的 UserContext 对象
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 1. 【正确方式】从请求属性中获取所有路径变量
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (httpServletRequest == null) {
            throw new IllegalStateException("Current request is not an HttpServletRequest");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String userId = (pathVariables != null) ? pathVariables.get("userId") : null;

        // 2. 校验 userId 是否获取到
        if (userId == null) {
            // 如果在某些非标准的请求中（如Filter转发）路径变量解析失败，可以有一个备用方案，但通常不应依赖它。
            // 对于Controller的正常调用，这里应该总能获取到。
            throw new IllegalArgumentException("User ID could not be resolved from the request path.");
        }

        // 3. 从请求头中获取其他信息（这部分逻辑不变）
        String vipHeader = webRequest.getHeader("X-User-Vip");
        boolean isVip = Boolean.parseBoolean(vipHeader);

        String labelsHeader = webRequest.getHeader("X-User-Labels");
        List<String> labels = (labelsHeader == null || labelsHeader.isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(labelsHeader.split(","));

        // 4. 构建并返回 UserContext 对象
        return new UserContext(userId, isVip, labels);
    }
}
