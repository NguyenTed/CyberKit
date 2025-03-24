package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tools")
@RequiredArgsConstructor
public class ToolController {
    private final ToolService toolService;
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping
    public List<ToolResponse> getAllTools() {
        return toolService.getAllTools();
    }

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("version") String version,
            @RequestParam("frontendPath") String frontendPath,
            @RequestParam("controllerClass") String controllerClass,
            @RequestParam("basePath") String basePath
    ) {
        ToolUploadRequest request = new ToolUploadRequest();
        request.setName(name);
        request.setDescription(description);
        request.setVersion(version);
        request.setFrontendPath(frontendPath);
        request.setControllerClass(controllerClass);
        request.setBasePath(basePath);

        try {
            toolService.uploadTool(file, request);
            return ResponseEntity.ok("Tool uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
