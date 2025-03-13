package com.cyberkit.cyberkit_server.repository;

import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    public AccountEntity findByEmail(String email);
    public  boolean existsByEmail(String email);
}
