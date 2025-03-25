package com.cyberkit.cyberkit_server.config;

import org.pf4j.DefaultExtensionFinder;
import org.pf4j.ExtensionFactory;
import org.pf4j.ExtensionFinder;
import org.pf4j.spring.SpringExtensionFactory;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;

public class ToolManager extends SpringPluginManager {
    private final ApplicationContext applicationContext;

    public ToolManager(ApplicationContext applicationContext) {
        super(Paths.get("tools")); // your plugin folder
        this.applicationContext = applicationContext;
        this.setApplicationContext(applicationContext); // required by pf4j-spring
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this, true); // ✅ uses autowire = true
    }

    @Override
    protected ExtensionFinder createExtensionFinder() {
        return new DefaultExtensionFinder(this); // ✅ avoids needing pf4j-maven-plugin
    }
}
