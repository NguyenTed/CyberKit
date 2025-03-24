package com.cyberkit.cyberkit_server.config;

import org.springframework.context.ApplicationContext;
import org.pf4j.spring.SpringPluginManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ToolManager extends SpringPluginManager {
    public ToolManager() {
        super(Paths.get("tools"));
    }
}
