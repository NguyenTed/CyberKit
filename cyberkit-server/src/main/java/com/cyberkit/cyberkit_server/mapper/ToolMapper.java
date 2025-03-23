package com.cyberkit.cyberkit_server.mapper;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import com.cyberkit.cyberkit_server.dto.request.ToolUploadRequest;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ToolMapper {
    ToolResponse toToolResponse(ToolEntity tool);
    ToolEntity toToolEntity(ToolUploadRequest request);
}
