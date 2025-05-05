package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public interface ToolCategoryService {
    List<ToolCategoryResponse> getAllToolCategories();
    List<ToolResponse> getToolsByCategoryFiltered(UUID categoryId, Jwt jwt);
}
