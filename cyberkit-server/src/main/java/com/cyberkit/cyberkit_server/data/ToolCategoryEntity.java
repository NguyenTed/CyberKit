package com.cyberkit.cyberkit_server.data;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;

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
