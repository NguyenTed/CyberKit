package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tools")
@Data
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
    @Column(name = "is_enabled")
    private boolean enabled;
    @Column(name = "is_premium")
    private boolean premium;

    @Column(nullable = false)
    private String backendPath;

    @Column(nullable = false)
    private String frontendPath;

    @Column(name = "controller_class")
    private String controllerClass;

    @Column(name = "base_path")
    private String basePath;

}
