package com.cyberkit.cyberkit_server.plugin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class PluginManager {
    private final Path pluginDir;
    private final Map<String, PluginWrapper> plugins = new ConcurrentHashMap<>();

    public PluginManager(Path pluginDir) {
        this.pluginDir = pluginDir;
    }

    public PluginWrapper getPlugin(String pluginId) {
        return plugins.get(pluginId);
    }

    public Collection<PluginWrapper> getAllPlugins() {
        return plugins.values();
    }

    public PluginWrapper loadPlugin(Path jarPath, String pluginId) throws Exception {
        if (plugins.containsKey(pluginId)) {
            throw new IllegalStateException("Plugin already loaded: " + pluginId);
        }

        PluginClassLoader classLoader = new PluginClassLoader(jarPath);
        log.info("PluginManager.loadPlugin, jarPath: " + jarPath);
        PluginWrapper wrapper = new PluginWrapper(pluginId, jarPath, classLoader);

        plugins.put(pluginId, wrapper);
        return wrapper;
    }

    public void unloadPlugin(String pluginId) {
        PluginWrapper wrapper = plugins.remove(pluginId);
        if (wrapper != null) {
            try {
                wrapper.getClassLoader().close(); // Unload classes
                log.info("Plugin unloaded: {}", pluginId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.warn("Plugin not found to unload: {}", pluginId);
        }
    }


    public PluginWrapper reloadPlugin(String pluginId, Path newJarPath) throws Exception {
        // Ensure plugin is unloaded first
        unloadPlugin(pluginId);

        // Defensive check (optional)
        if (plugins.containsKey(pluginId)) {
            throw new IllegalStateException("Plugin still exists after unload: " + pluginId);
        }

        // Load the updated plugin JAR with isolated classloader
        PluginClassLoader classLoader = new PluginClassLoader(newJarPath);
        PluginWrapper wrapper = new PluginWrapper(pluginId, newJarPath, classLoader);

        plugins.put(pluginId, wrapper);
        log.info("Reloaded plugin: {}", pluginId);
        return wrapper;
    }
}

