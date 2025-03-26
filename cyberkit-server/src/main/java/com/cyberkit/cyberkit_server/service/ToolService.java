package com.cyberkit.cyberkit_server.service;
import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class ToolService {
    private final PluginManager pluginManager;
    private final ToolRepository toolRepository;
    private final ToolMapper toolMapper;

    @Autowired
    private ApplicationContext context;

    public List<ToolResponse> getAllTools() {
        return toolRepository.findAll().stream().map(toolMapper::toToolResponse).toList();
    }

    public Optional<ToolEntity> getToolById(String id) {
        try {
            return toolRepository.findById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return Optional.empty(); // invalid UUID format
        }
    }

    public void uploadTool(MultipartFile backend, MultipartFile frontendZip, ToolUploadRequest request) throws IOException {
        // Save backend .jar to /tools
        String jarName = backend.getOriginalFilename();
        Path jarPath = Paths.get("tools", jarName);
        Files.createDirectories(jarPath.getParent());
        Files.copy(backend.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        // Extract frontend .zip to /tools/{toolId}/frontend/
        String toolId = request.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        Path frontendPath = Paths.get("tools", toolId, "frontend");
        Files.createDirectories(frontendPath);

        try (ZipInputStream zis = new ZipInputStream(frontendZip.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = frontendPath.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        // Load and start plugin
        String pluginId = pluginManager.loadPlugin(jarPath);
        pluginManager.startPlugin(pluginId);

        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = wrapper.getDescriptor();
        ClassLoader pluginClassLoader = wrapper.getPluginClassLoader();

        // Register controller
        try {
            String controllerClassName = request.getControllerClass();
            Class<?> controllerClass = pluginClassLoader.loadClass(controllerClassName);
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
            configurableContext.getBeanFactory().autowireBean(controllerInstance);
            configurableContext.getBeanFactory().registerSingleton("tool_" + toolId, controllerInstance);

            RequestMappingHandlerMapping mapping = configurableContext.getBean(RequestMappingHandlerMapping.class);

            for (Method method : controllerClass.getDeclaredMethods()) {
                RequestMappingInfo info = null;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    for (String path : method.getAnnotation(GetMapping.class).value()) {
                        info = RequestMappingInfo
                                .paths(request.getBasePath() + path)
                                .methods(RequestMethod.GET)
                                .build();
                        mapping.registerMapping(info, controllerInstance, method);
                    }
                }

                if (method.isAnnotationPresent(PostMapping.class)) {
                    for (String path : method.getAnnotation(PostMapping.class).value()) {
                        info = RequestMappingInfo
                                .paths(request.getBasePath() + path)
                                .methods(RequestMethod.POST)
                                .build();
                        mapping.registerMapping(info, controllerInstance, method);
                    }
                }

                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping rm = method.getAnnotation(RequestMapping.class);
                    for (String path : rm.value()) {
                        info = RequestMappingInfo
                                .paths(request.getBasePath() + path)
                                .methods(rm.method())
                                .build();
                        mapping.registerMapping(info, controllerInstance, method);
                    }
                }
            }

            System.out.println("✅ Controller registered: " + controllerClassName);
        } catch (Exception e) {
            System.err.println("❌ Failed to register plugin controller.");
            e.printStackTrace();
        }

        // Save tool metadata
        ToolEntity tool = new ToolEntity();
        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setVersion(descriptor.getVersion());
        tool.setPremium(false);
        tool.setEnabled(false);
        tool.setBackendPath(jarPath.toString());
        tool.setFrontendPath("/plugins/" + toolId + "/frontend/index.html"); // updated!
        tool.setControllerClass(request.getControllerClass());
        tool.setBasePath(request.getBasePath());

        toolRepository.save(tool);
    }
}
