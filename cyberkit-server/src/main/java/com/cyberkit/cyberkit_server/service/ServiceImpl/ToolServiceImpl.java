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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import java.util.stream.Stream;
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
    public List<ToolResponse> getToolsFiltered(Jwt jwt, Boolean premium, Boolean enabled, UUID categoryId) {
        boolean isAdmin = jwt != null &&
                jwt.getClaimAsStringList("authorities").contains("ROLE_ADMIN");

        List<ToolEntity> tools = isAdmin
                ? toolRepository.findAll()
                : toolRepository.findAllByEnabled(true);

        return tools.stream()
                .map(toolMapper::toToolResponse)
                .toList();
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
    public void uploadTool(MultipartFile combinedZip, ToolUploadRequest request) throws Exception {
        ToolEntity tool = ToolEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .version(request.getVersion())
                .icon(request.getIcon())
                .premium(false)
                .enabled(false)
                .category(toolCategoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Tool category not found")))
                .backendPath("")
                .frontendPath("")
                .build();

        toolRepository.save(tool);
        String toolId = String.valueOf(tool.getId());

        // Define final storage paths
        Path pluginRootDir = Paths.get("tools", toolId);
        Path backendDir = pluginRootDir.resolve("backend");
        Path frontendDir = pluginRootDir.resolve("frontend");
        Files.createDirectories(pluginRootDir);

        // 1. Unzip to a temp directory
        Path tempDir = Files.createTempDirectory("tool-upload");
        unzip(combinedZip, tempDir);

        // 2. Validate structure
        Path rootDir = Files.list(tempDir)
                .filter(Files::isDirectory)
                .findFirst()
                .orElseThrow(() -> new IOException("No root folder found in ZIP"));

        Path backendSourceDir = rootDir.resolve("tool-backend");
        Path frontendSourceDir = rootDir.resolve("tool-frontend");

        // 3. Move backend jar to final location
        Files.createDirectories(backendDir);
        Path jarFile = Files.list(backendSourceDir)
                .filter(f -> f.toString().endsWith(".jar"))
                .findFirst()
                .orElseThrow(() -> new IOException("No .jar file found in tool-backend/"));

        Path targetJarPath = backendDir.resolve(toolId + ".jar");
        Files.copy(jarFile, targetJarPath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Move frontend files to final location
        Files.createDirectories(frontendDir);
        FileSystemUtils.copyRecursively(frontendSourceDir, frontendDir);

        // 5. Load plugin
        PluginWrapper wrapper = pluginManager.loadPlugin(targetJarPath, toolId);
        loadAndRegisterPluginService(toolId, targetJarPath, wrapper.getClassLoader());

        // 6. Save updated paths to DB
        tool.setBackendPath(targetJarPath.toString());
        tool.setFrontendPath("/plugins/" + toolId + "/frontend/index.html");
        toolRepository.save(tool);

        // 7. Clean up
        FileSystemUtils.deleteRecursively(tempDir);
    }

    private void unzip(MultipartFile zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();

                // 🚫 Skip macOS metadata
                if (entryName.startsWith("__MACOSX") || entryName.endsWith(".DS_Store")) {
                    continue;
                }

                Path newPath = targetDir.resolve(entryName).normalize();
                if (!newPath.startsWith(targetDir)) {
                    throw new IOException("Invalid zip entry: " + entryName);
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
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
    @Transactional
    public void deleteTool(String toolId) throws IOException {
        UUID id = UUID.fromString(toolId);
        ToolEntity tool = toolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolId));

        // 🔥 Clear favorite references
        tool.getUsers().forEach(user -> user.getFavouriteTools().remove(tool));
        tool.getUsers().clear(); // Optional, but helps GC and avoids accidental flush

        toolRepository.save(tool); // Needed to flush relationship changes

        pluginManager.unloadPlugin(toolId);

        Path pluginRoot = Paths.get("tools", toolId);
        if (Files.exists(pluginRoot)) {
            FileUtils.deleteDirectory(pluginRoot.toFile());
        }

        toolRepository.delete(tool);
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