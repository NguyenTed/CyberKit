package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tools")
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String version;
    private String description;
    private String icon;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "premium")
    private boolean premium;

    @Column(nullable = false)
    private String backendPath;

    @Column(nullable = false)
    private String frontendPath;

    @Column(name = "plugin_id", unique = true)
    private String pluginId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ToolCategoryEntity category;
}
