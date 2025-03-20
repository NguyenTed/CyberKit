package com.cyberkit.cyberkit_server.util;

import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;

public class OAuth2UserInfoMapper {
    public static RegisterDTO convertGithubUserInfo(String username, String email){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName(username);
        registerDTO.setEmail(email);
        registerDTO.setRole(RoleEnum.USER);
        return registerDTO;
    }
}
