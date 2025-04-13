package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tool_categories")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToolCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String icon;
}
