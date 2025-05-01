package com.cyberkit.cyberkit_server.service.ServiceImpl;
import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.plugin.PluginClassLoader;
import com.cyberkit.cyberkit_server.plugin.PluginManager;
import com.cyberkit.cyberkit_server.plugin.PluginWrapper;
import com.cyberkit.cyberkit_server.repository.ToolCategoryRepository;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.ToolService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import com.cyberkit.cyberkit_server.util.StringUtil;
import com.cyberkit.pluginservice.PluginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToolServiceImpl implements ToolService {
    private final ToolRepository toolRepository;
    private final ToolCategoryRepository toolCategoryRepository;
    private final PluginManager pluginManager;
    private final ToolMapper toolMapper;
    private final AccountService accountService;

    @Override
    public ToolResponse getToolById(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        return toolMapper.toToolResponse(tool);
    }

    @Override
    public List<ToolResponse> getAllTools() {
        var tools = toolRepository.findAll();
        return tools.stream().map(toolMapper::toToolResponse).toList();
    }

    @Override
    public void togglePremiumTool(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        var currentPremium = tool.isPremium();
        tool.setPremium(!currentPremium);
        toolRepository.save(tool);
    }

    @Override
    public void toggleEnabledTool(String id) {
        var tool = toolRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tool not found"));
        var currentEnabled = tool.isEnabled();
        tool.setEnabled(!currentEnabled);
        toolRepository.save(tool);
    }

    @Override
    public void uploadTool(MultipartFile backendJar, MultipartFile frontendZip, ToolUploadRequest request) throws Exception {
        ToolEntity tool = ToolEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .version(request.getVersion())
                .icon(request.getIcon())
                .premium(false)
                .enabled(false)
                .category(toolCategoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Tool category not found")))
                .backendPath("")
                .frontendPath("")
                .build();

        toolRepository.save(tool);
        String toolId = String.valueOf(tool.getId());

        // Define base paths
        Path pluginRootDir = Paths.get("tools", toolId);
        Path backendDir = pluginRootDir.resolve("backend");
        Path frontendDir = pluginRootDir.resolve("frontend");

        // 1. Save backend jar to /tools/{pluginId}/backend/{pluginId}.jar
        Files.createDirectories(backendDir);
        Path jarPath = backendDir.resolve(toolId + ".jar");
        Files.copy(backendJar.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        // 2. Extract frontend zip to /tools/{pluginId}/frontend/
        extractFrontendZip(frontendZip, frontendDir);

        PluginWrapper wrapper = pluginManager.loadPlugin(jarPath, toolId);
        PluginClassLoader classLoader = wrapper.getClassLoader();

        loadAndRegisterPluginService(toolId, jarPath, classLoader);

        // 5. Save metadata
        tool.setBackendPath(jarPath.toString());
        tool.setFrontendPath("/plugins/" + toolId + "/frontend/index.html");
        toolRepository.save(tool);
    }

    private void extractFrontendZip (MultipartFile frontendZip, Path frontendDir) throws IOException {
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
    }

    @Override
    public Map<String, Object> executeTool (String toolId, String action, Map<String, Object> input) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        ToolEntity toolEntity = toolRepository.findById(UUID.fromString(toolId)).orElseThrow(() -> new RuntimeException("Tool not found"));
        // 1. Get the plugin wrapper
        PluginWrapper wrapper = pluginManager.getPlugin(toolId);
        if (wrapper == null) {
            throw new IllegalStateException("Plugin not loaded: " + toolEntity.getName());
        }

        // 2. Get class loader
        PluginClassLoader classLoader = wrapper.getClassLoader();

        // 3. Load the class implementing PluginService
        Class<?> clazz = findPluginServiceImpl(Path.of(toolEntity.getBackendPath()), classLoader);

        // 4. Ensure it implements PluginService
        Object serviceInstance = clazz.getDeclaredConstructor().newInstance();
        if (!(serviceInstance instanceof PluginService)) {
            throw new IllegalArgumentException("Class does not implement PluginService");
        }

        // 5. Cast to the shared interface
        PluginService pluginService = (PluginService) serviceInstance;

        // 6. Call the method (example for hash)
        Map<String, Object> result = pluginService.execute(action, input);
        log.info("Result of plugin: {}", result);

        return result;
    }

    @Override
    public void updateTool(String toolId, ToolUploadRequest request, MultipartFile newJar, MultipartFile newFrontendZip) throws Exception {
        ToolEntity tool = toolRepository.findById(UUID.fromString(toolId))
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setVersion(request.getVersion());
        tool.setIcon(request.getIcon());
        tool.setCategory(toolCategoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Tool category not found")));
        toolRepository.save(tool);
        log.info("✅ Plugin information updated: {}", tool.getName());

        Path pluginRootDir = Paths.get("tools", toolId);
        Path backendDir = pluginRootDir.resolve("backend");
        Path frontendDir = pluginRootDir.resolve("frontend");
        Path jarPath = backendDir.resolve(toolId + ".jar");

        if (newFrontendZip != null && !newFrontendZip.isEmpty()) {
            // ♻️ Replace frontend
            FileUtils.deleteDirectory(frontendDir.toFile());
            extractFrontendZip(newFrontendZip, frontendDir);
        }

        if (newJar != null && !newJar.isEmpty()) {
            // 🔌 Unload if loaded
            if (pluginManager.getPlugin(toolId) != null) {
                pluginManager.unloadPlugin(toolId);
            }

            // ♻️ Replace backend
            Files.createDirectories(backendDir);
            Files.copy(newJar.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("✅ Currently loaded plugins: {}", pluginManager.getAllPlugins().stream()
                    .map(PluginWrapper::getId)
                    .toList());

            // 5. Reload backend plugin
            PluginWrapper wrapper = pluginManager.reloadPlugin(toolId, jarPath);
            ClassLoader classLoader = wrapper.getClassLoader();

            loadAndRegisterPluginService(toolId, jarPath, classLoader);
            log.info("✅ Plugin development updated and reloaded: {}", tool.getName());
        }
    }

    @Override
    public void deleteTool(String toolId) throws IOException {
        ToolEntity tool = toolRepository.findById(UUID.fromString(toolId))
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolId));

        String toolName = tool.getName();

        pluginManager.unloadPlugin(toolId);

        Path pluginRoot = Paths.get("tools", toolId);
        if (Files.exists(pluginRoot)) {
            FileUtils.deleteDirectory(pluginRoot.toFile());
            log.info("Deleted plugin files: {}", pluginRoot);
        }

        toolRepository.delete(tool);
        log.info("Plugin deleted successfully: {}", toolName);
    }

    private Class<?> findPluginServiceImpl(Path jarPath, PluginClassLoader classLoader) throws IOException, ClassNotFoundException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");

                    Class<?> cls = classLoader.loadClass(className);
                    for (Class<?> iface : cls.getInterfaces()) {
                        if (iface.getName().equals("com.cyberkit.pluginservice.PluginService")) {
                            log.info("✅ Found PluginService: " + className);
                            return cls;
                        }
                    }
                }
            }
        }
        throw new ClassNotFoundException("No class implementing com.cyberkit.pluginservice.PluginService found in " + jarPath);
    }

    private void loadAndRegisterPluginService(String toolId, Path jarPath, ClassLoader classLoader) throws Exception {
        // Scan for PluginService implementation
        // Opens the JAR as a stream so you can read each file inside.
        try (JarInputStream jarStream = new JarInputStream(Files.newInputStream(jarPath))) {
            // Iterates through every entry (file or folder) inside the JAR.
            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                // Skips folders and only continues with .class files.
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    // Converts com/example/MyClass.class → com.example.MyClass (a fully-qualified class name).
                    String className = name.replace('/', '.').replace(".class", "");
                    // Dynamically loads the class using your custom plugin class loader.
                    Class<?> clazz = classLoader.loadClass(className);

                    // Makes sure:
                    // The class implements PluginService
                    // The class is not abstract
                    if (PluginService.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                        // Instantiates the plugin with a no-arg constructor.
                        PluginService service = (PluginService) clazz.getDeclaredConstructor().newInstance();
                        log.info("✅ Loaded PluginService: {}", className);
                        return;
                    }
                }
            }
        }

        throw new IllegalStateException("No PluginServices implementation found in plugin: " + toolId);
    }

    @Override
    public List<ToolResponse> searchTools(String keyWord) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        UserDTO userDTO = null;
        List<ToolEntity> toolEntities = new ArrayList<>();
        if(!email.equals("anonymousUser")){
            userDTO = accountService.getUserInfoByEmail(email);
        }
        if(userDTO!=null && userDTO.getRole().equals(RoleEnum.ADMIN)){
            toolEntities = toolRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyWord,keyWord);
        }
        else if(userDTO!=null && userDTO.isPremium()==true){
            toolEntities = toolRepository.findEnabledToolsByKeyword(keyWord);
        }
        else{
            toolEntities= toolRepository.findNotPremiumEnabledToolsByKeyword(keyWord);
        }
        toolEntities = sortToolsByKeyword(toolEntities,keyWord);
        return toolEntities.stream().map(toolMapper::toToolResponse).toList();
    }

    public List<ToolEntity> sortToolsByKeyword(List<ToolEntity> tools, String keyword) {
        tools.sort(Comparator.comparingInt(t -> StringUtil.minDistance( keyword.toLowerCase(),t.getName().toLowerCase())));
        return tools;
    }
}