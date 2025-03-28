package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import com.cyberkit.cyberkit_server.mapper.ToolCategoryMapper;
import com.cyberkit.cyberkit_server.repository.ToolCategoryRepository;
import com.cyberkit.cyberkit_server.service.ToolCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolCategoryServiceImpl implements ToolCategoryService {
    private final ToolCategoryRepository toolCategoryRepository;
    private final ToolCategoryMapper toolCategoryMapper;

    public ToolCategoryServiceImpl(ToolCategoryRepository toolCategoryRepository, ToolCategoryMapper toolCategoryMapper) {
        this.toolCategoryRepository = toolCategoryRepository;
        this.toolCategoryMapper = toolCategoryMapper;
    }

    @Override
    public List<ToolCategoryResponse> getAllToolCategories() {
        return toolCategoryRepository.findAll().stream().map(toolCategoryMapper::toToolCategoryResponse).toList();
    }
}
