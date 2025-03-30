package com.cyberkit.cyberkit_server.plugin;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.pluginservice.PluginService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Component
@Slf4j
public class PluginStartupLoader {
    private final PluginManager pluginManager;
    private final ToolRepository toolRepository;

    public PluginStartupLoader(PluginManager pluginManager, ToolRepository toolRepository) {
        this.pluginManager = pluginManager;
        this.toolRepository = toolRepository;
    }

    @PostConstruct
    public void loadPluginsAtStartup() {
        List<ToolEntity> tools = toolRepository.findAll();

        for (ToolEntity tool : tools) {
            try {
                String toolId = String.valueOf(tool.getId());
                Path jarPath = Paths.get(tool.getBackendPath());

                PluginWrapper wrapper = pluginManager.loadPlugin(jarPath, toolId);
                ClassLoader classLoader = wrapper.getClassLoader();

                try (JarInputStream jarStream = new JarInputStream(Files.newInputStream(jarPath))) {
                    JarEntry entry;
                    while ((entry = jarStream.getNextJarEntry()) != null) {
                        String name = entry.getName();
                        if (!entry.isDirectory() && name.endsWith(".class")) {
                            String className = name.replace('/', '.').replace(".class", "");
                            Class<?> clazz = classLoader.loadClass(className);

                            // Check if it implements PluginService
                            if (PluginService.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                                PluginService service = (PluginService) clazz.getDeclaredConstructor().newInstance();
                                log.info("✅ Loaded PluginService: {}", className);
                            }
                        }
                    }
                }

                log.info("✅ Loaded plugin on startup: {}", tool.getName());
            } catch (Exception e) {
                log.error("❌ Failed to load plugin on startup: {}", tool.getName());
            }
        }
    }
}
