package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @RequestParam("frontendPath") String frontendPath
    ) {
        ToolUploadRequest request = new ToolUploadRequest();
        request.setName(name);
        request.setDescription(description);
        request.setVersion(version);
        request.setFrontendPath(frontendPath);

        try {
            toolService.uploadTool(file, request);
            return ResponseEntity.ok("Tool uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
