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
public class ToolCategoryResponse {
    private UUID id;
    private String name;
    private String icon;
}
