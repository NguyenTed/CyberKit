package com.cyberkit.cyberkit_server.plugin;
import lombok.Getter;
import java.nio.file.Path;

@Getter
public class PluginWrapper {
    private final String id;
    private final Path jarPath;
    private final PluginClassLoader classLoader;

    public PluginWrapper(String id, Path jarPath, PluginClassLoader classLoader) {
        this.id = id;
        this.jarPath = jarPath;
        this.classLoader = classLoader;
    }
}

