package com.cyberkit.cyberkit_server.repository;

import com.cyberkit.cyberkit_server.data.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, UUID> {
    public List<ToolEntity> findByPremium(boolean premium);
}
