package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/tools")
@RequiredArgsConstructor
public class ToolController {
    private final ToolService toolService;

    @GetMapping
    public RestResponse<List<ToolResponse>> getAllTools() {
        log.info("ToolController.getAllTools");
        var tools = toolService.getAllTools();
        for (ToolResponse tool : tools) {
            log.info(String.valueOf(tool.isPremium()));
        }
        return RestResponse.<List<ToolResponse>>builder()
                .data(tools)
                .build();
    }

    @GetMapping("/{id}")
    public RestResponse<ToolResponse> getToolById(@PathVariable String id) {
        log.info("ToolController.getToolById");
        return RestResponse.<ToolResponse>builder()
                .data(toolService.getToolById(id))
                .build();
    }

    @PostMapping("/togglePremium/{id}")
    public RestResponse<Void> togglePremiumTool(@PathVariable String id) {
        log.info("ToolController.togglePremiumTool");
        toolService.togglePremiumTool(id);
        return RestResponse.<Void>builder().message("Tool premium toggled").build();
    }

    @PostMapping("/toggleEnabled/{id}")
    public RestResponse<Void> toggleEnabledTool(@PathVariable String id) {
        log.info("ToolController.toggleEnabledTool");
        toolService.toggleEnabledTool(id);
        return RestResponse.<Void>builder().message("Tool ability toggled").build();
    }

    @PostMapping
    public RestResponse<Void> uploadTool(
            @RequestParam("backend") MultipartFile backend,
            @RequestParam("frontend") MultipartFile frontend,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("version") String version,
            @RequestParam("icon") String icon,
            @RequestParam("categoryId") UUID categoryId
    ) {
        log.info("ToolController.upload");
        ToolUploadRequest request = new ToolUploadRequest();
        request.setName(name);
        request.setDescription(description);
        request.setVersion(version);
        request.setIcon(icon);
        request.setCategoryId(categoryId);

        try {
            toolService.uploadTool(backend, frontend, request);
            return RestResponse.<Void>builder()
                    .statusCode(200)
                    .message("Upload successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.<Void>builder()
                    .statusCode(500)
                    .error("Upload failed" + e.getMessage())
                    .build();
        }
    }

    @PutMapping("/updateTool/{toolId}")
    public RestResponse<Void> updateTool(
            @RequestParam("backend") MultipartFile backend,
            @RequestParam("frontend") MultipartFile frontend,
            @PathVariable("toolId") String toolId
    ) {
        log.info("ToolController.updateTool");
        try {
            toolService.updateTool(backend, frontend, toolId);
            return RestResponse.<Void>builder()
                    .statusCode(200)
                    .message("Update tool successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.<Void>builder()
                    .statusCode(500)
                    .error("Fail to update tool: " + e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/{toolId}")
    public RestResponse<Void> deleteTool(@PathVariable String toolId) {
        log.info("ToolController.deleteTool");
        try {
            toolService.deleteTool(toolId);
            return RestResponse.<Void>builder()
                    .statusCode(200)
                    .message("Delete tool successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.<Void>builder()
                    .statusCode(500)
                    .error("Fail to delete tool: " + e.getMessage())
                    .build();
        }
    }
}
