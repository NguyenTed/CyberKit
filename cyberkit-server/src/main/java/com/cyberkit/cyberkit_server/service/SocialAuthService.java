package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.GithubSocialDTO;

import java.io.IOException;


public interface SocialAuthService {
    public GithubSocialDTO fetchGitHubUserProfile(String code) throws IOException;
}
