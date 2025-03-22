package com.cyberkit.cyberkit_server.config;

import org.pf4j.DefaultPluginManager;

import java.nio.file.Paths;

public class ToolManager extends DefaultPluginManager {
    public ToolManager() {
        super(Paths.get("tools")); // <- your custom tool folder
    }
}
