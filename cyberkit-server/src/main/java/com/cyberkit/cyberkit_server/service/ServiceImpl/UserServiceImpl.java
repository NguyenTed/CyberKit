package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.AdminEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @Override
    public UserDTO saveUser(RegisterDTO registerDTO) {
        //Check email exists
        if(accountRepository.existsByEmail(registerDTO.getEmail())){
            throw new GeneralAllException("Email is existed!!");
        }

        // Create UserEntity
        UserEntity userEntity = modelMapper.map(registerDTO,UserEntity.class);

        // Create Account Entity
        AccountEntity account= new AccountEntity();
        account.setRole(RoleEnum.USER);
        account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        account.setEmail(registerDTO.getEmail());
        account.setUser(userEntity);
        accountRepository.save(account);
        // Set default role is user and not premium.
        //userEntity.setPremium(false);
        // Decode the password before insert user into database
        UserDTO userDTO = modelMapper.map(userEntity,UserDTO.class);
        return userDTO;
    }


    @Override
    public UserDTO getCurrentUser(String email) {
        UserEntity userEntity = null;//userRepository.findByEmail(email);
        UserDTO userDTO = modelMapper.map(userEntity,UserDTO.class);
//        userDTO.setPassword(null);
//        userDTO.setId(null);
        return userDTO;
    }

    @Override
    public void updateUserToken(String token, String email) {
        UserEntity userEntity= null;//userRepository.findByEmail(email);
        if(userEntity != null){
            //userEntity.setRefreshToken(token);
            userRepository.save(userEntity);
        }
        else{
            throw new GeneralAllException("Not existing the user!");
        }
    }
}
