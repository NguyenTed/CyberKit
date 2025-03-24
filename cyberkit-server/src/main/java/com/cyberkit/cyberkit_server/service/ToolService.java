package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.config.ToolManager;
import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToolService {
    private final PluginManager pluginManager;
    private final ToolRepository toolRepository;
    private final ToolMapper toolMapper;

    public List<ToolResponse> getAllTools () {
        return toolRepository.findAll().stream().map(toolMapper::toToolResponse).toList();
    }

    public void uploadTool (MultipartFile file, ToolUploadRequest request) throws IOException {
        String jarName = file.getOriginalFilename();
        Path jarPath = Paths.get("tools", jarName);
        Files.createDirectories(jarPath.getParent());
        Files.copy(file.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        // Load plugin and get pluginId
        String pluginId = pluginManager.loadPlugin(jarPath);

        // Start plugin
        pluginManager.startPlugin(pluginId);

        // Get metadata
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = wrapper.getDescriptor();

        // Save tool metadata
        ToolEntity tool = new ToolEntity();
        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setVersion(descriptor.getVersion());
        tool.setPremium(false);
        tool.setEnabled(false);
        tool.setBackendPath(jarPath.toString());
        tool.setFrontendPath(request.getFrontendPath());

        toolRepository.save(tool);
    }
}
