package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tools")
@Data
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
    private boolean isEnabled;
    private boolean isPremium;

    @Column(nullable = false)
    private String backendPath;

    @Column(nullable = false)
    private String frontendPath;
}
