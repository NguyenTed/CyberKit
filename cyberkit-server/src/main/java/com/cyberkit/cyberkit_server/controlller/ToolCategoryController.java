package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.service.ToolCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tool-categories")
@RequiredArgsConstructor
public class ToolCategoryController {
    private final ToolCategoryService toolCategoryService;

    @GetMapping
    RestResponse<List<ToolCategoryResponse>> getAllToolCategories() {
        return RestResponse.<List<ToolCategoryResponse>>builder().data(toolCategoryService.getAllToolCategories()).build();
    }
}
