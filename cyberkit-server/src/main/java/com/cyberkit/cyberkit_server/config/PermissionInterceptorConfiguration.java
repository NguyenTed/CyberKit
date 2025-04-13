package com.cyberkit.cyberkit_server.config;

import com.cyberkit.cyberkit_server.security.PermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/api/v1/tools/execute/**",
                "/api/v1/tools/update/**",
                "/api/v1/tools/download/**",
                "/api/v1/tool-categories/**",
                "/api/v1/tools/search/**",
                 "/api/v1/auth/login",
                "/api/v1/auth/signup",
                "/api/v1/auth/account",
                "/api/v1/auth/account/waiting",
                "/api/v1/auth/refresh",
                "/api/v1/auth/logout",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/v1/auth/",
                "/api/v1/auth/github-login",
                "/api/v1/auth/github-code/**",
                "/api/v1/payment/**",
                "/api/v1/subscriptions",
                "/api/v1/subscriptions/**",
                "/api/v1/tools",
                "/api/v1/users/**"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}