package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;

public interface UserService {
    public UserDTO saveUser(RegisterDTO registerDTO);
    public UserDTO getCurrentUser(String email);
    public void updateUserToken(String token, String email);
}
