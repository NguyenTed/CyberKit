package com.cyberkit.cyberkit_server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class Oauth2LoginConfig {
    @Value("${spring.jwt.key}")
    private String jwtKey;


    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(github());
    }

    private ClientRegistration github(){
         return CommonOAuth2Provider.GITHUB.getBuilder("github")
                 .clientId(githubClientId)
                 .clientSecret(githubClientSecret)
                 .scope("user:email")
                 .build();
        }
}
