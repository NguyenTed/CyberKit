package com.cyberkit.cyberkit_server.repository;

import com.cyberkit.cyberkit_server.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<UserEntity, Long> {
}
