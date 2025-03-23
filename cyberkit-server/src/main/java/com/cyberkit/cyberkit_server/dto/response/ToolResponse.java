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
    private boolean isEnabled;
    private boolean isPremium;
    private String frontendPath;
}
