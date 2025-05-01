package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public RestResponse<List<ToolResponse>> getToolsByCategory(@PathVariable("categoryId") String categoryId) {
        System.out.println("🔍 Looking for tools in category ID: " + categoryId);
        List<ToolResponse> tools = toolCategoryService.getToolsByCategory(categoryId);
        return RestResponse.<List<ToolResponse>>builder()
                .data(tools)
                .build();
    }
}
