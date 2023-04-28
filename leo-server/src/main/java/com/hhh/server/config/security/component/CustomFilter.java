package com.hhh.server.config.security.component;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

/**
 * 权限控制 根据请求url分析请求所需的角色
 *
 * @author HHH
 * @date 2022/2/15 @Version 1.0.0
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 获取请求url
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        if (antPathMatcher.match("/leoo/**", requestUrl)) {
            return SecurityConfig.createList("ROLE_ADMIN");
        } else if (antPathMatcher.match("/leo/**", requestUrl)) {
            return SecurityConfig.createList("ROLE_USER", "ROLE_ADMIN");
        }
        // 没匹配的url默认登录即可访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
