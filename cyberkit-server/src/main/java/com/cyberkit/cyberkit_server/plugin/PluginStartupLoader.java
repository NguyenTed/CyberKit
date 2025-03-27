package com.cyberkit.cyberkit_server.plugin;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Component
@Slf4j
public class PluginStartupLoader {

    private final PluginManager pluginManager;
    private final ToolRepository toolRepository;
    private final ApplicationContext context;

    public PluginStartupLoader(PluginManager pluginManager, ToolRepository toolRepository, ApplicationContext context) {
        this.pluginManager = pluginManager;
        this.toolRepository = toolRepository;
        this.context = context;
    }

    @PostConstruct
    public void loadPluginsAtStartup() {
        List<ToolEntity> tools = toolRepository.findAll();

        for (ToolEntity tool : tools) {
            try {
                String pluginId = tool.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                Path jarPath = Paths.get(tool.getBackendPath());

                PluginWrapper wrapper = pluginManager.loadPlugin(jarPath, pluginId);
                ClassLoader classLoader = wrapper.getClassLoader();

                // 2. Unregister controller
                String controllerClassName = findControllerClassName(jarPath);
                Class<?> controllerClass = classLoader.loadClass(controllerClassName);
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) context;
                configCtx.getBeanFactory().autowireBean(controllerInstance);

                // Check if bean is already registered
                String beanName = "tool_" + pluginId;
                if (!configCtx.getBeanFactory().containsSingleton(beanName)) {
                    configCtx.getBeanFactory().registerSingleton(beanName, controllerInstance);
                    registerPluginController(controllerInstance, controllerClass, tool.getPluginId());
                }

                System.out.println("✅ Loaded plugin on startup: " + tool.getName());

            } catch (Exception e) {
                System.err.println("❌ Failed to load plugin on startup: " + tool.getName());
                e.printStackTrace();
            }
        }
    }

    private void registerPluginController(Object controllerInstance, Class<?> controllerClass, String pluginId) {
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> existingMappings = mapping.getHandlerMethods();

        for (Method method : controllerClass.getDeclaredMethods()) {
            RequestMappingInfo info = null;

            if (method.isAnnotationPresent(GetMapping.class)) {
                for (String path : method.getAnnotation(GetMapping.class).value()) {
                    var endpoint = "/api/tool/" + pluginId + path;
                    info = RequestMappingInfo
                            .paths(endpoint)
                            .methods(RequestMethod.GET)
                            .build();

                    if (!isAlreadyMapped(existingMappings, info)) {
                        mapping.registerMapping(info, controllerInstance, method);
                        System.out.println("🔗 Registered GET: " + path);
                    } else {
                        System.out.println("⚠️ Skipping already mapped GET: " + path);
                    }
                }
            }

            if (method.isAnnotationPresent(PostMapping.class)) {
                for (String path : method.getAnnotation(PostMapping.class).value()) {
                    var endpoint = "/api/tool/" + pluginId + path;
                    info = RequestMappingInfo
                            .paths(endpoint) // basePath might be "" or "/api/tool"
                            .methods(RequestMethod.POST)
                            .build();
                    if (!isAlreadyMapped(existingMappings, info)) {
                        mapping.registerMapping(info, controllerInstance, method);
                        System.out.println("🔗 Registered POST: " + path);
                    } else {
                        System.out.println("⚠️ Skipping already mapped POST: " + path);
                    }
                }
            }

            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                for (String path : rm.value()) {
                    info = RequestMappingInfo.paths(path).methods(rm.method()).build();
                    if (!isAlreadyMapped(existingMappings, info)) {
                        mapping.registerMapping(info, controllerInstance, method);
                        System.out.println("🔗 Registered: " + path);
                    } else {
                        System.out.println("⚠️ Skipping already mapped: " + path);
                    }
                }
            }
        }

        System.out.println("✅ Controller registered: " + controllerClass.getName());
    }

    private boolean isAlreadyMapped(Map<RequestMappingInfo, HandlerMethod> mappings, RequestMappingInfo newInfo) {
        for (RequestMappingInfo existingInfo : mappings.keySet()) {
            var existingPatterns = existingInfo.getPathPatternsCondition();
            var newPatterns = newInfo.getPathPatternsCondition();
            var existingMethods = existingInfo.getMethodsCondition();
            var newMethods = newInfo.getMethodsCondition();

            // If pattern condition or method condition is null, skip comparison
            if (existingPatterns == null || newPatterns == null || existingMethods == null || newMethods == null) {
                continue;
            }

            if (existingPatterns.equals(newPatterns) && existingMethods.equals(newMethods)) {
                return true;
            }
        }
        return false;
    }

    public String findControllerClassName(Path jarPath) throws Exception {
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null);
             JarInputStream jarStream = new JarInputStream(Files.newInputStream(jarPath))) {

            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    try {
                        Class<?> cls = classLoader.loadClass(className);
                        System.out.println("🔍 Inspecting class: " + className);

                        for (Annotation annotation : cls.getAnnotations()) {
                            String annotationName = annotation.annotationType().getName();
                            if (annotationName.equals("org.springframework.web.bind.annotation.RestController") ||
                                    annotationName.equals("org.springframework.stereotype.Controller")) {
                                System.out.println("✅ Found controller: " + className);
                                return className; // THIS LINE IS CRITICAL
                            }
                        }
                    } catch (Throwable t) {
                        System.err.println("⚠️ Failed to load class: " + className + ": " + t.getMessage());
                    }
                }
            }
        }
        return null; // Only if no controller found
    }
}
