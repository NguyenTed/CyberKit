package com.cyberkit.cyberkit_server.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConfig {
    private final Path pluginDirectory;

    public PluginConfig(String pluginDir) {
        this.pluginDirectory = Paths.get(pluginDir);
    }

    public Path getPluginDirectory() {
        return pluginDirectory;
    }
}
