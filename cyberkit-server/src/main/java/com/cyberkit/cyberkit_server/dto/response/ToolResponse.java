package com.cyberkit.cyberkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolResponse {
    private UUID id;
    private String name;
    private String description;
    private String version;
    private String icon;
    private boolean enabled;
    private boolean premium;
    private String frontendPath;
    private String categoryId;
    private String categoryName;
}
