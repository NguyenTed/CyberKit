package com.cyberkit.cyberkit_server.config;

import org.springframework.context.ApplicationContext;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public PluginManager pluginManager(ApplicationContext context) {
        ToolManager manager = new ToolManager();
        manager.setApplicationContext(context); // ✅ manually set Spring context
        return manager;
    }
}
