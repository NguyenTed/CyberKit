package com.cyberkit.cyberkit_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolUploadRequest {
    private String name;
    private String description;
    private String version;
    private String icon;
    private UUID categoryId;
}
