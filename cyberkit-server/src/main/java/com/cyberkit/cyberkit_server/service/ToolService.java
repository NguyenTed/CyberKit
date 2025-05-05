package com.cyberkit.cyberkit_server.service;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ToolService {
    ToolResponse getToolById(String id);
    List<ToolResponse> getToolsFiltered(Jwt jwt, Boolean premium, Boolean enabled, UUID categoryId);
    void togglePremiumTool(String id);
    void toggleEnabledTool(String id);
    void uploadTool(MultipartFile backendJar, MultipartFile frontendZip, ToolUploadRequest request) throws Exception;
    Map<String, Object> executeTool (String toolId, String action, Map<String, Object> input) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException;
    void updateTool(String toolId, ToolUploadRequest request, MultipartFile newJar, MultipartFile newFrontendZip) throws Exception;
    void deleteTool(String toolId) throws IOException;
    List<ToolResponse> searchTools(String keyWord);
}
