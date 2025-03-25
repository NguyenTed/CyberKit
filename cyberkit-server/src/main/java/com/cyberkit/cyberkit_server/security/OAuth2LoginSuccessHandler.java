package com.cyberkit.cyberkit_server.security;

import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.dto.response.ResLoginDTO;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.UserService;
import com.cyberkit.cyberkit_server.util.OAuth2UserInfoMapper;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${spring.jwt.refresh-expiration-in-seconds}")
    private int refreshJwtExpiration;
    private final ObjectMapper mapper;
    private final AccountService accountService;
    private final SecurityUtil securityUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.get("email").toString();
            String name = attributes.get("login").toString();

            if(!accountService.existsAccountByEmail(email)){
                RegisterDTO registerDTO = OAuth2UserInfoMapper.convertGithubUserInfo(name,email);
                accountService.createAccount(registerDTO);

            }
            String accessToken = securityUtil.createAccessToken(email);
            String refreshToken = securityUtil.createRefreshToken(email);
            accountService.updateRefreshToken(refreshToken,email);
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setMaxAge(refreshJwtExpiration);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
            response.sendRedirect("http://localhost:5173/oauth/callback?access_token="+accessToken);
        }
    }

}

