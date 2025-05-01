package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;

import java.util.List;

public interface ToolCategoryService {
    List<ToolCategoryResponse> getAllToolCategories();
    List<ToolResponse> getToolsByCategory(String categoryId);
}
