package com.cyberkit.cyberkit_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // ❌ Turn off completely
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/plugins/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 This is crucial!
                        .requestMatchers(HttpMethod.GET, "/api/v1/tools").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tools").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tools/download/**").hasAnyRole("ADMIN")
                        .requestMatchers("/api/tools/**","/api/v1/tools/search/**").permitAll()
                        .requestMatchers("/api/bcrypt/hash", "/api/bcrypt/compare", "/api/ascii/text-to-binary", "/api/ascii/binary-to-text").permitAll()
                        .requestMatchers("api/v1/auth/github-login","api/v1/auth/github-code/**").permitAll()
                        .requestMatchers("/api/v1/auth/login","api/v1/auth/signup","api/v1/auth/refresh", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/account").hasAnyRole("ADMIN")
                        .requestMatchers("/api/v1/users","/api/v1/users/**").hasAnyRole("USER")
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
                    oauth2.disable();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}