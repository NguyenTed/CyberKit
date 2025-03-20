package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

// Advance method overwrite bean using by component ( convert the first digit to lower)
@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {
    private final AccountRepository accountRepository;

    public UserDetailCustom(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountRepository.findByEmail(username);

        // Return the User is child class of UserDetails
        return new User(
                accountEntity.getEmail()
                ,accountEntity.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+accountEntity.getRole()))
        );

    }


}
