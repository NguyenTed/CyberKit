package com.cyberkit.cyberkit_server.service;

import java.io.IOException;

import com.cyberkit.cyberkit_server.dto.GithubSocialDTO;

public interface SocialAuthService {
    public GithubSocialDTO fetchGitHubUserProfile(String code) throws IOException;
}
