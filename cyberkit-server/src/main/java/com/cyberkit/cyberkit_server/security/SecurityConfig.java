package com.cyberkit.cyberkit_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler,
                                                   OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                                                   CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login","/api/v1/auth/register","/api/v1/auth/refresh","/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html","api/v1/oauth2").permitAll()
                        .requestMatchers("api/v1/account").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer((oauth2)->oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
//                .exceptionHandling(
//                        exceptions -> exceptions
//                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) //401
//                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) //403
                .formLogin(f->f.disable())
                .oauth2Login(oauth2 -> {
                    oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                            .successHandler(oAuth2LoginSuccessHandler);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}