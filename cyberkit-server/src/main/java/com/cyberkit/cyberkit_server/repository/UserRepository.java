package com.cyberkit.cyberkit_server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyberkit.cyberkit_server.data.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByIdAndFavouriteToolsId(Long userId, UUID toolId);
}
