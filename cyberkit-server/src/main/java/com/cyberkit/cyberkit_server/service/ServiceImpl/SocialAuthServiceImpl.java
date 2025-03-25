package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.dto.GithubSocialDTO;
import com.cyberkit.cyberkit_server.service.SocialAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocialAuthServiceImpl implements SocialAuthService {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final GithubEmailFetcher githubEmailFetcher;

    public SocialAuthServiceImpl(GithubEmailFetcher githubEmailFetcher) {
        this.githubEmailFetcher = githubEmailFetcher;
    }

    @Override
    public GithubSocialDTO fetchGitHubUserProfile(String code) throws IOException {
        String accessToken= getAccessToken(code);
        String url = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            GithubSocialDTO githubSocialDTO = new GithubSocialDTO();
            githubSocialDTO.setName(jsonNode.get("login").asText());
            githubSocialDTO.setEmail(githubEmailFetcher.fetchPrimaryEmailAddress(accessToken));
            githubSocialDTO.setAvatarUrl(jsonNode.get("avatar_url").asText());

            return githubSocialDTO;
        }
        throw new IOException("Failed to fetch user profile from GitHub");
    }

    private String getAccessToken(String code) throws IOException{
        String url = "https://github.com/login/oauth/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        // Tạo body cho request
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("redirect_uri", redirectUri);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        }

        throw new IOException("Failed to get access token from GitHub");
    }
}
