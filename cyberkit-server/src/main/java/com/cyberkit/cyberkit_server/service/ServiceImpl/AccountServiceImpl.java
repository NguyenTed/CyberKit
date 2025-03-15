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
import com.cyberkit.cyberkit_server.repository.UserRepository;
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
    private final UserRepository userRepository;


    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    @Transactional
    @Override
    public UserDTO createAccount(RegisterDTO registerDTO) {
        //Check email exists
        if(accountRepository.existsByEmail(registerDTO.getEmail())){
            throw new GeneralAllException("Email is existed!!");
        }
        // Create UserEntity
        UserEntity userEntity = modelMapper.map(registerDTO,UserEntity.class);
        userEntity.setPremium(false);
        userEntity.setRefreshToken(null);

        // Create Account Entity
        AccountEntity account= new AccountEntity();
        account.setRole(RoleEnum.USER);
        // Logging by username, password
        if(registerDTO.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        }
        account.setEmail(registerDTO.getEmail());
        //Save user entity before
        userEntity = userRepository.save(userEntity);
        //Set saved user entity for account entity
        account.setUser(userEntity);
        accountRepository.save(account);

        UserDTO userDTO = modelMapper.map(userEntity,UserDTO.class);
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

    @Override
    public Boolean existsAccountByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public UserDTO getUserInfoByEmail(String email) {
        AccountEntity accountEntity= accountRepository.findByEmail(email);
        if(accountEntity == null){
            throw new GeneralAllException("Invalid email!!");
        }
        AbstractUserEntity userEntity = accountRepository.findByEmail(email).getUser();
        if(userEntity==null){
            throw new GeneralAllException("Invalid email!!");
        }
        UserDTO userInfo = modelMapper.map(userEntity, UserDTO.class);
        userInfo.setEmail(email);
        userInfo.setRole(userEntity instanceof AdminEntity ? RoleEnum.ADMIN : RoleEnum.USER);
        return userInfo;
    }

    @Override
    public Boolean checkValidRefreshToken(String refreshToken, String email) {
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        if(accountEntity==null){
            throw new GeneralAllException("Invalid Emails!!");
        }
        return accountEntity.getRefreshToken().equals(refreshToken);
    }


}
