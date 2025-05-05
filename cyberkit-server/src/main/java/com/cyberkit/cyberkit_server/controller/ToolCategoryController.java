package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ToolCategoryController {
    private final ToolCategoryService toolCategoryService;

    @GetMapping
    RestResponse<List<ToolCategoryResponse>> getAllToolCategories() {
        return RestResponse.<List<ToolCategoryResponse>>builder().data(toolCategoryService.getAllToolCategories()).build();
    }

    @GetMapping("/{categoryId}/tools")
    public RestResponse<List<ToolResponse>> getToolsByCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return RestResponse.<List<ToolResponse>>builder()
                .data(toolCategoryService.getToolsByCategoryFiltered(categoryId, jwt))
                .build();
    }
}
