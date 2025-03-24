package com.cyberkit.cyberkit_server.service;
import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import java.lang.reflect.Method;

@Service
@RequiredArgsConstructor
public class ToolService {
    private final PluginManager pluginManager;
    private final ToolRepository toolRepository;
    private final ToolMapper toolMapper;

    @Autowired
    private ApplicationContext context;

    public List<ToolResponse> getAllTools() {
        return toolRepository.findAll().stream().map(toolMapper::toToolResponse).toList();
    }

    public void uploadTool(MultipartFile file, ToolUploadRequest request) throws IOException {
        String jarName = file.getOriginalFilename();
        Path jarPath = Paths.get("tools", jarName);
        Files.createDirectories(jarPath.getParent());
        Files.copy(file.getInputStream(), jarPath, StandardCopyOption.REPLACE_EXISTING);

        String pluginId = pluginManager.loadPlugin(jarPath);
        pluginManager.startPlugin(pluginId);
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = wrapper.getDescriptor();
        ClassLoader pluginClassLoader = wrapper.getPluginClassLoader();

        String controllerClassName = request.getControllerClass();
        String basePath = request.getBasePath();
        String beanName = "tool_" + request.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        try {
            Class<?> controllerClass = pluginClassLoader.loadClass(controllerClassName);
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
            configurableContext.getBeanFactory().autowireBean(controllerInstance);
            configurableContext.getBeanFactory().registerSingleton(beanName, controllerInstance);

            RequestMappingHandlerMapping mapping = configurableContext.getBean(RequestMappingHandlerMapping.class);

            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    for (String path : getMapping.value()) {
                        RequestMappingInfo info = RequestMappingInfo
                                .paths(basePath + path)
                                .methods(RequestMethod.GET)
                                .build();
                        mapping.registerMapping(info, controllerInstance, method);
                        System.out.println("🔗 Registered GET: " + path);
                    }
                }

                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping postMapping = method.getAnnotation(PostMapping.class);
                    for (String path : postMapping.value()) {
                        RequestMappingInfo mappingInfo = RequestMappingInfo
                                .paths(basePath + path)
                                .methods(RequestMethod.POST)
                                .build();
                        mapping.registerMapping(mappingInfo, controllerInstance, method);
                        System.out.println("🔗 Registered POST: " + basePath + path);
                    }
                }

                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    for (String path : requestMapping.value()) {
                        RequestMappingInfo info = RequestMappingInfo
                                .paths(basePath + path)
                                .methods(requestMapping.method())
                                .build();
                        mapping.registerMapping(info, controllerInstance, method);
                        System.out.println("🔗 Registered: " + path);
                    }
                }
            }

//            // Register mappings for the new bean
//            mapping.afterPropertiesSet();
//
//            // Log only the mappings registered for this controller
//            mapping.getHandlerMethods().forEach((info, method) -> {
//                if (method.getBean().equals(controllerInstance)) {
//                    System.out.println("🔗 Registered [" + info.getMethodsCondition() + "] " + info.getPatternsCondition());
//                }
//            });

            System.out.println("✅ Controller registered: " + controllerClass.getName());

        } catch (Exception e) {
            System.err.println("❌ Failed to register plugin controller.");
            e.printStackTrace();
        }

        ToolEntity tool = new ToolEntity();
        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setVersion(descriptor.getVersion());
        tool.setPremium(false);
        tool.setEnabled(false);
        tool.setBackendPath(jarPath.toString());
        tool.setFrontendPath(request.getFrontendPath());
        tool.setControllerClass(controllerClassName);
        tool.setBasePath(basePath);

        toolRepository.save(tool);
    }
}
