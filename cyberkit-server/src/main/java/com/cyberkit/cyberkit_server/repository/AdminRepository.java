package com.cyberkit.cyberkit_server.repository;


import com.cyberkit.cyberkit_server.data.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
}
