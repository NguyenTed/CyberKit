package com.cyberkit.cyberkit_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyberkit.cyberkit_server.data.AdminEntity;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {}
