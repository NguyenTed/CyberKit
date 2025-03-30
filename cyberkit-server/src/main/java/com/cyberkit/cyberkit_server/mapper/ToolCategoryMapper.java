package com.cyberkit.cyberkit_server.mapper;

import com.cyberkit.cyberkit_server.data.ToolCategoryEntity;
import com.cyberkit.cyberkit_server.dto.response.ToolCategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ToolCategoryMapper {
    ToolCategoryResponse toToolCategoryResponse(ToolCategoryEntity entity);
}
