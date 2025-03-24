package com.cyberkit.cyberkit_server.repository;

import com.cyberkit.cyberkit_server.data.SubscriptionEntity;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    List<SubscriptionEntity> findByUser_IdAndStatusOrderByEndDateDesc(Long userId, SubscriptionStatus status);
}
