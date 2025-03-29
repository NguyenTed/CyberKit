package com.cyberkit.cyberkit_server.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginManagerConfig {

    @Bean
    public PluginConfig pluginConfig() {
        return new PluginConfig("tools"); // where plugins are stored
    }

    @Bean
    public PluginManager pluginManager(PluginConfig config) {
        return new PluginManager(config.getPluginDirectory());
    }
}

