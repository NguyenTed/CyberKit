package com.cyberkit.cyberkit_server.mapper;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToolMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "enabled", target = "enabled")
    @Mapping(source = "premium", target = "premium")
    @Mapping(source = "frontendPath", target = "frontendPath")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ToolResponse toToolResponse(ToolEntity tool);
    ToolEntity toToolEntity(ToolUploadRequest request);
}
