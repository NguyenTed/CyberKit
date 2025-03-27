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

@Slf4j
@RestController
@RequestMapping("/api/v1/tools")
@RequiredArgsConstructor
public class ToolController {
    private final ToolService toolService;

    @GetMapping
    public RestResponse<List<ToolResponse>> getAllTools() {
        log.info("ToolController.getAllTools");
        return RestResponse.<List<ToolResponse>>builder()
                .data(toolService.getAllTools())
                .build();
    }

    @GetMapping("/{id}")
    public RestResponse<ToolResponse> getToolById(@PathVariable String id) {
        log.info("ToolController.getToolById");
        return RestResponse.<ToolResponse>builder()
                .data(toolService.getToolById(id))
                .build();
    }

    @PostMapping
    public RestResponse<String> upload(
            @RequestParam("backend") MultipartFile backend,
            @RequestParam("frontend") MultipartFile frontend,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("version") String version,
            @RequestParam("frontendPath") String frontendPath,
            @RequestParam("controllerClass") String controllerClass,
            @RequestParam("basePath") String basePath
    ) {
        log.info("ToolController.upload");
        ToolUploadRequest request = new ToolUploadRequest();
        request.setName(name);
        request.setDescription(description);
        request.setVersion(version);
        request.setFrontendPath(frontendPath);
        request.setControllerClass(controllerClass);
        request.setBasePath(basePath);

        try {
            toolService.uploadTool(backend, frontend, request);
            return RestResponse.<String>builder()
                    .data("Upload successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.<String>builder()
                    .statusCode(500)
                    .error("Upload failed" + e.getMessage())
                    .build();
        }
    }
}
