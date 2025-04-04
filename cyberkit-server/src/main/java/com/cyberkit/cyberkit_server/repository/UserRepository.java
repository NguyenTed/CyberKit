package com.cyberkit.cyberkit_server.repository;

import com.cyberkit.cyberkit_server.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByIdAndToolsId(Long userId, UUID toolId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_favourite_tool WHERE user_id = :userId AND tool_id = :toolId ;", nativeQuery = true)
    void removeToolFromFavoritesNative(@Param("userId") Long userId, @Param("toolId") UUID toolId);



}
