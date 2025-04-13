package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;

import java.util.Date;
import java.util.List;

public interface UserService {
    public Boolean checkValidSubscription(Date endDate, Long userId);
    public void likeTool(String toolId);
    public List<ToolResponse> getFavouriteTools();
    public Boolean checkExistingSubscription();
}
