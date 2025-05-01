package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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

    @Column(columnDefinition = "TEXT")
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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ToolCategoryEntity category;

    @ManyToMany(mappedBy = "favouriteTools", fetch = FetchType.LAZY)
    private List<UserEntity> users = new ArrayList<>();
}
