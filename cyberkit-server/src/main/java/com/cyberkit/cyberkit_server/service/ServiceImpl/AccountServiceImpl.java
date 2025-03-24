package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.SubscriptionEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.GithubSocialDTO;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository  accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;


    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

        this.userService = userService;
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
        UserDTO userDTO=  modelMapper.map(accountEntity,UserDTO.class);
        AbstractUserEntity abstractUserEntity = accountEntity.getUser();
        userDTO.setName(abstractUserEntity.getName());
        userDTO.setGender(abstractUserEntity.getGender());

        Date birthOfDate = abstractUserEntity.getDateOfBirth();
        if(birthOfDate!=null){
            LocalDate localDate = birthOfDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = localDate.format(formatter);
            userDTO.setDateOfBirth(formattedDate);
        }
        userDTO.setPremium(false);
        if(abstractUserEntity instanceof  UserEntity){
            userDTO.setPremium(((UserEntity) abstractUserEntity).isPremium());
            //Check the valid expired premium
            if(userDTO.isPremium()&& !userService.checkValidSubscription(abstractUserEntity.getId())){
                userDTO.setPremium(false);
            }
        }
        return userDTO;
    }

    @Override
    public Boolean checkValidRefreshToken(String refreshToken, String email) {
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        if(accountEntity==null){
            throw new GeneralAllException("Invalid cookie!!");
        }
        return accountEntity.getRefreshToken().equals(refreshToken);
    }

    @Override
    public UserDTO createGithubAccount(GithubSocialDTO githubSocialDTO) {
        if(!existsAccountByEmail(githubSocialDTO.getEmail())){
             RegisterDTO registerDTO = modelMapper.map(githubSocialDTO,RegisterDTO.class);
             return createAccount(registerDTO);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(githubSocialDTO.getEmail());
        userDTO.setName(githubSocialDTO.getName());
        return userDTO;
    }

}
