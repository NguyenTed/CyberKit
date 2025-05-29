package com.cyberkit.cyberkit_server.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absPath = Paths.get("tools").toAbsolutePath().toUri().toString();
        System.out.println("📁 Mapping /plugins/** → " + absPath);

        registry.addResourceHandler("/plugins/**")
                .addResourceLocations("file:tools/")
                .setCachePeriod(0);
    }
}
