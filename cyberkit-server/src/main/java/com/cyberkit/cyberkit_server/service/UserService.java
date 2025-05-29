package com.cyberkit.cyberkit_server.service;

import java.util.Date;
import java.util.List;

import com.cyberkit.cyberkit_server.dto.response.ToolResponse;

public interface UserService {
    public Boolean checkValidSubscription(Date endDate, Long userId);

    public void addToolToFavoriteTool(String toolId);

    public void removeToolFromFavoriteTool(String toolId);

    public List<ToolResponse> getMyFavoriteTools();

    public Boolean checkExistingSubscription();
}
