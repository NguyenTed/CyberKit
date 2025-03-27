package com.cyberkit.cyberkit_server.plugin;

import java.nio.file.Path;

public class PluginWrapper {
    private final String id;
    private final Path jarPath;
    private final PluginClassLoader classLoader;

    public PluginWrapper(String id, Path jarPath, PluginClassLoader classLoader) {
        this.id = id;
        this.jarPath = jarPath;
        this.classLoader = classLoader;
    }

    public String getId() {
        return id;
    }

    public Path getJarPath() {
        return jarPath;
    }

    public PluginClassLoader getClassLoader() {
        return classLoader;
    }
}

