package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.response.ToolResponse;

import java.util.Date;
import java.util.List;

public interface UserService {
    public Boolean checkValidSubscription(Date endDate, Long userId);
    public void addToolToFavoriteTool(String toolId);
    public void removeToolFromFavoriteTool(String toolId);
    public List<ToolResponse> getMyFavoriteTools();
    public Boolean checkExistingSubscription();
}
