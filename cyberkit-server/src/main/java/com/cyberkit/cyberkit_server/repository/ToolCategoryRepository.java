package com.cyberkit.cyberkit_server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyberkit.cyberkit_server.data.ToolCategoryEntity;

@Repository
public interface ToolCategoryRepository extends JpaRepository<ToolCategoryEntity, UUID> {}
