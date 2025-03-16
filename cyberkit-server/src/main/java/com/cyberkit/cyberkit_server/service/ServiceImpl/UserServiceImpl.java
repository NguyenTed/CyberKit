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

}
