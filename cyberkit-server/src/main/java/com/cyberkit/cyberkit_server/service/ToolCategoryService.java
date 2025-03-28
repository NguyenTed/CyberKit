package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;

import java.util.List;

public interface ToolCategoryService {
    List<ToolCategoryResponse> getAllToolCategories();
}
