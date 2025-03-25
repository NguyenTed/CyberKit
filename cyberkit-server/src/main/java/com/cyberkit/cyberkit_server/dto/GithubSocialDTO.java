package com.cyberkit.cyberkit_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GithubSocialDTO {
    private String name;
    private String email;
    private String avatarUrl;

}
