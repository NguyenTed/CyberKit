package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;

public interface UserService {
    public Boolean checkValidSubscription(Long id);
}
