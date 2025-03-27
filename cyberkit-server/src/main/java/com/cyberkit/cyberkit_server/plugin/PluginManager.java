package com.cyberkit.cyberkit_server.plugin;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PluginManager {
    private final Path pluginDir;
    private final Map<String, PluginWrapper> plugins = new ConcurrentHashMap<>();

    public PluginManager(Path pluginDir) {
        this.pluginDir = pluginDir;
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

    public PluginWrapper getPlugin(String pluginId) {
        return plugins.get(pluginId);
    }

    public void unloadPlugin(String pluginId) {
        PluginWrapper wrapper = plugins.remove(pluginId);
        if (wrapper != null) {
            try {
                wrapper.getClassLoader().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<PluginWrapper> getAllPlugins() {
        return plugins.values();
    }
}

