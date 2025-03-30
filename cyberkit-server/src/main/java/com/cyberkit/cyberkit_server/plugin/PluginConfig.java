package com.cyberkit.cyberkit_server.plugin;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class PluginConfig {
    private final Path pluginDirectory;

    public PluginConfig(String pluginDir) {
        this.pluginDirectory = Paths.get(pluginDir);
    }

}
