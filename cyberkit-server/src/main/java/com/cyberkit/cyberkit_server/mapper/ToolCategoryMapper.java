package com.cyberkit.cyberkit_server.mapper;

import org.mapstruct.Mapper;

import com.cyberkit.cyberkit_server.data.ToolCategoryEntity;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;

@Mapper(componentModel = "spring")
public interface ToolCategoryMapper {
    ToolCategoryResponse toToolCategoryResponse(ToolCategoryEntity entity);
}
