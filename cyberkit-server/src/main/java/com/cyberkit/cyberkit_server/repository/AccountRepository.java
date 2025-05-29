package com.cyberkit.cyberkit_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyberkit.cyberkit_server.data.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
