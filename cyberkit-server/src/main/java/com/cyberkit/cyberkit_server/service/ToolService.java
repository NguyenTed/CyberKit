package com.cyberkit.cyberkit_server.service;
import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.plugin.PluginManager;
import com.cyberkit.cyberkit_server.plugin.PluginWrapper;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToolService {

    private final ApplicationContext context;
    private final ToolRepository toolRepository;
    private final PluginManager pluginManager;
    private final ToolMapper toolMapper;

    public ToolResponse getToolById(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        return toolMapper.toToolResponse(tool);
    }

    public List<ToolResponse> getAllTools() {
        var tools = toolRepository.findAll();
        return tools.stream().map(toolMapper::toToolResponse).toList();
    }

    public void togglePremiumTool(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        var currentPremium = tool.isPremium();
        tool.setPremium(!currentPremium);
        toolRepository.save(tool);
    }

    public void toggleEnabledTool(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        var currentEnabled = tool.isEnabled();
        tool.setEnabled(!currentEnabled);
        toolRepository.save(tool);
    }

    public void uploadTool(MultipartFile backendJar, MultipartFile frontendZip, ToolUploadRequest request) throws Exception {
        String toolId = request.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // Define base paths
        Path pluginRootDir = Paths.get("tools", toolId);
        Path backendDir = pluginRootDir.resolve("backend");
        Path frontendDir = pluginRootDir.resolve("frontend");

        // 1. Save backend jar to /tools/{toolId}/backend/{toolId}.jar
        Files.createDirectories(backendDir);
        Path jarPath = backendDir.resolve(toolId + ".jar");
        Files.copy(backendJar.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        // 2. Extract frontend zip to /tools/{toolId}/frontend/
        Files.createDirectories(frontendDir);
        try (ZipInputStream zis = new ZipInputStream(frontendZip.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = frontendDir.resolve(entry.getName()).normalize();
                if (!entryPath.startsWith(frontendDir)) {
                    throw new SecurityException("Bad zip entry: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        // 3. Load backend plugin jar
        PluginWrapper wrapper = pluginManager.loadPlugin(jarPath, toolId);
        ClassLoader classLoader = wrapper.getClassLoader();

        // 4. Dynamically register plugin controller
        try {
            String controllerClassName = request.getControllerClass();
            Class<?> controllerClass = classLoader.loadClass(controllerClassName);
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) context;
            configCtx.getBeanFactory().autowireBean(controllerInstance);
            configCtx.getBeanFactory().registerSingleton("tool_" + toolId, controllerInstance);

            System.out.println("🧪 Methods in controller:");
            for (Method method : controllerClass.getDeclaredMethods()) {
                System.out.println(" - " + method.getName());
            }

            registerPluginController(controllerInstance, controllerClass, request.getBasePath());
        } catch (Exception e) {
            log.error("❌ Failed to register plugin controller.");
            e.printStackTrace();
        }

        // 5. Save metadata
        ToolEntity tool = new ToolEntity();
        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setVersion(request.getVersion());
        tool.setPremium(false);
        tool.setEnabled(false);
        tool.setBackendPath(jarPath.toString());
        tool.setFrontendPath("/plugins/" + toolId + "/frontend/index.html");
        tool.setControllerClass(request.getControllerClass());
        tool.setBasePath(request.getBasePath()); // optional, no longer prepended

        toolRepository.save(tool);
    }

    public void updateTool(MultipartFile newJar, MultipartFile newFrontendZip, String toolId) throws Exception {
        var tool = toolRepository.findById(UUID.fromString(toolId)).orElseThrow(() -> new RuntimeException("Tool not found"));
        var pluginId = tool.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        // 1. Paths
        Path pluginRootDir = Paths.get("tools", pluginId);
        Path backendDir = pluginRootDir.resolve("backend");
        Path frontendDir = pluginRootDir.resolve("frontend");
        Path jarPath = backendDir.resolve(pluginId + ".jar");

        // 2. Unload plugin
        pluginManager.unloadPlugin(pluginId);

        // 3. Replace backend JAR
        Files.createDirectories(backendDir);
        Files.copy(newJar.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Replace frontend UI
        FileUtils.deleteDirectory(frontendDir.toFile());
        Files.createDirectories(frontendDir);
        try (ZipInputStream zis = new ZipInputStream(newFrontendZip.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = frontendDir.resolve(entry.getName()).normalize();
                if (!entryPath.startsWith(frontendDir)) throw new SecurityException("Bad zip entry: " + entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        // 5. Reload backend plugin
        PluginWrapper wrapper = pluginManager.reloadPlugin(pluginId, jarPath);
        ClassLoader classLoader = wrapper.getClassLoader();

        // 7. Register controller
        String controllerClassName = tool.getControllerClass();  // e.g. com.cyberkit.texttobinary.TextAsciiToolController
        Class<?> controllerClass = classLoader.loadClass(controllerClassName);

        ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) context;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configCtx.getBeanFactory();
        RequestMappingHandlerMapping mapping = configCtx.getBean(RequestMappingHandlerMapping.class);

        // Remove existing bean if exists
        String beanName = "tool_" + toolId;
        if (beanFactory.containsSingleton(beanName)) {
            beanFactory.destroySingleton(beanName);
        }

        // Remove old mappings
        List<HandlerMethod> toRemove = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.getHandlerMethods().entrySet()) {
            if (entry.getValue().getBeanType().getName().equals(controllerClass.getName())) {
                toRemove.add(entry.getValue());
                mapping.unregisterMapping(entry.getKey());
            }
        }

        // Register new controller
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
        beanFactory.autowireBean(controllerInstance);
        beanFactory.registerSingleton(beanName, controllerInstance);
        registerPluginController(controllerInstance, controllerClass, tool.getBasePath());


        System.out.println("✅ Plugin updated and reloaded: " + pluginId);
    }

    public void deleteTool(String toolId) throws Exception {
        // 1. Get the plugin
        ToolEntity tool = toolRepository.findById(UUID.fromString(toolId))
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolId));
        var pluginId = tool.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // 2. Unregister controller
        String controllerClassName = tool.getControllerClass();
        ClassLoader classLoader = pluginManager.getPlugin(pluginId).getClassLoader();
        Class<?> controllerClass = classLoader.loadClass(controllerClassName);

        ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) context;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configCtx.getBeanFactory();
        RequestMappingHandlerMapping mapping = configCtx.getBean(RequestMappingHandlerMapping.class);

        // Remove mappings
        List<RequestMappingInfo> toUnregister = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.getHandlerMethods().entrySet()) {
            if (entry.getValue().getBeanType().getName().equals(controllerClass.getName())) {
                toUnregister.add(entry.getKey());
            }
        }
        for (RequestMappingInfo info : toUnregister) {
            mapping.unregisterMapping(info);
            log.info("Unregistered: ", info.getName());
        }

        // Remove Spring bean
        String beanName = "tool_" + pluginId;
        if (beanFactory.containsSingleton(beanName)) {
            beanFactory.destroySingleton(beanName);
        }

        // 3. Unload plugin
        pluginManager.unloadPlugin(pluginId);

        // 4. Delete files
        Path pluginRootDir = Paths.get("tools", pluginId);
        FileUtils.deleteDirectory(pluginRootDir.toFile());

        // 5. Remove from DB
        toolRepository.delete(tool);

        System.out.println("🗑️ Plugin deleted successfully: " + pluginId);
    }

    private void registerPluginController(Object controllerInstance, Class<?> controllerClass, String basePath) {
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);

        for (Method method : controllerClass.getDeclaredMethods()) {
            RequestMappingInfo info = null;

            if (method.isAnnotationPresent(GetMapping.class)) {
                String[] paths = method.getAnnotation(GetMapping.class).value();
                for (String path : paths) {
                    info = RequestMappingInfo
                            .paths(joinPaths(basePath, path))
                            .methods(RequestMethod.GET)
                            .build();
                    mapping.registerMapping(info, controllerInstance, method);
                    log.info("🔗 Registered GET: {}", info.getPatternValues());
                }
            }

            if (method.isAnnotationPresent(PostMapping.class)) {
                String[] paths = method.getAnnotation(PostMapping.class).value();
                for (String path : paths) {
                    info = RequestMappingInfo
                            .paths(joinPaths(basePath, path))
                            .methods(RequestMethod.POST)
                            .build();
                    mapping.registerMapping(info, controllerInstance, method);
                    log.info("🔗 Registered POST: {}", info.getPatternValues());
                }
            }

            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                for (String path : rm.value()) {
                    info = RequestMappingInfo
                            .paths(joinPaths(basePath, path))
                            .methods(rm.method())
                            .build();
                    mapping.registerMapping(info, controllerInstance, method);
                    log.info("🔗 Registered: {}", info.getPatternValues());
                }
            }
        }

        log.info("✅ Controller registered: {}", controllerClass.getName());
    }

    private String joinPaths(String base, String path) {
        if (base == null) base = "";
        if (path == null) path = "";
        return (base + "/" + path).replaceAll("//+", "/");
    }
}