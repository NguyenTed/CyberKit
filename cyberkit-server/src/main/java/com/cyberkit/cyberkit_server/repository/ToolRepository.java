package com.cyberkit.cyberkit_server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cyberkit.cyberkit_server.data.ToolEntity;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, UUID> {
    @Query(
            value = "SELECT t.* FROM tools t WHERE t.enabled = true AND "
                    + " ( LOWER(t.name) ILIKE LOWER(CONCAT('%', :keyWord, '%')) "
                    + "OR LOWER(t.description) ILIKE LOWER(CONCAT('%', :keyWord, '%'))) "
                    + " LIMIT 6 ",
            nativeQuery = true)
    List<ToolEntity> findEnabledToolsByKeyword(@Param("keyWord") String keyWord);

    @Query(
            value = "SELECT t.* FROM tools t WHERE t.enabled = true AND t.premium = false AND "
                    + " ( LOWER(t.name) ILIKE LOWER(CONCAT('%', :keyWord, '%')) "
                    + "OR LOWER(t.description) ILIKE LOWER(CONCAT('%', :keyWord, '%')))"
                    + " LIMIT 6 ",
            nativeQuery = true)
    List<ToolEntity> findNotPremiumEnabledToolsByKeyword(@Param("keyWord") String keyWord);

    List<ToolEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<ToolEntity> findByCategoryId(UUID categoryId);

    List<ToolEntity> findByCategoryIdAndEnabledTrue(UUID categoryId);

    List<ToolEntity> findAllByEnabled(boolean enabled);
}
