package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.AdminEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.AdminRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository  accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;


    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }


    @Transactional
    @Override
    public UserDTO createAccount(RegisterDTO registerDTO) {
        //Check email exists
        if(accountRepository.existsByEmail(registerDTO.getEmail())){
            throw new GeneralAllException("Email is existed!!");
        }

//        // Create UserEntity
//        UserEntity userEntity = modelMapper.map(registerDTO,UserEntity.class);
//        userEntity.setPremium(false);
//        userEntity.setRefreshToken(null);
        // Create AdminEntity
        AdminEntity adminEntity = modelMapper.map(registerDTO,AdminEntity.class);

        // Create Account Entity
        AccountEntity account= new AccountEntity();
        account.setRole(RoleEnum.ADMIN);
        account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        account.setEmail(registerDTO.getEmail());
        account.setUser(adminEntity);
        accountRepository.save(account);
        // Set default role is user and not premium.

        // Decode the password before insert user into database
        UserDTO userDTO = modelMapper.map(adminEntity,UserDTO.class);
        return userDTO;
    }

    @Override
    public AbstractUserEntity getUserByEmail(String email) {
        AccountEntity account = accountRepository.findByEmail(email);
        if(account == null){
            throw  new GeneralAllException("Invalid email!");
        }
        return account.getUser();
    }

    @Override
    public void updateRefreshToken(String token, String email) {
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        if(accountEntity==null){
            throw  new GeneralAllException("Email invalid!!");
        }
        accountEntity.setRefreshToken(token);
        accountRepository.save(accountEntity);
    }

}
