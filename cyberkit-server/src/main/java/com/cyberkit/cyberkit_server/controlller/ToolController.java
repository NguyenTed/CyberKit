package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        System.out.println("ToolController.getAllTools");
        return toolService.getAllTools();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getToolById(@PathVariable String id) {
        System.out.println("ToolController.getToolById");
        Optional<ToolEntity> toolOpt = toolService.getToolById(id);

        if (toolOpt.isPresent()) {
            System.out.println("finish ToolController.getToolById");
            return ResponseEntity.ok(toolOpt.get());
        } else {
            System.out.println("finish (not found) ToolController.getToolById");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tool not found");
        }

    }

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam("backend") MultipartFile backend,
            @RequestParam("frontend") MultipartFile frontend,
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
            toolService.uploadTool(backend, frontend, request);
            return ResponseEntity.ok("Tool uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
