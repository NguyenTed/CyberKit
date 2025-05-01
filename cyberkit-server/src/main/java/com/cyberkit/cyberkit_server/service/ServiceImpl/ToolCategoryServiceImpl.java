package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.mapper.ToolCategoryMapper;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.repository.ToolCategoryRepository;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.cyberkit_server.service.ToolCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToolCategoryServiceImpl implements ToolCategoryService {
    private final ToolCategoryRepository toolCategoryRepository;
    private final ToolRepository toolRepository;
    private final ToolCategoryMapper toolCategoryMapper;
    private final ToolMapper toolMapper;

    @Override
    public List<ToolCategoryResponse> getAllToolCategories() {
        return toolCategoryRepository.findAll().stream().map(toolCategoryMapper::toToolCategoryResponse).toList();
    }

    @Override
    public List<ToolResponse> getToolsByCategory(String categoryId) {
        System.out.println("🔍 Looking for tools in category ID: " + categoryId);
        List<ToolEntity> tools = toolRepository.findByCategoryIdAndEnabledTrue(UUID.fromString(categoryId));
        return tools.stream().map(toolMapper::toToolResponse).toList();
    }
}
