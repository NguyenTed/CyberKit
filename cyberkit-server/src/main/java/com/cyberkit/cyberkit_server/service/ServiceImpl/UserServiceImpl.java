package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.AdminEntity;
import com.cyberkit.cyberkit_server.data.SubscriptionEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserServiceImpl(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Boolean checkValidSubscription(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        List<SubscriptionEntity> subscriptionEntityList=  subscriptionRepository.findByUser_IdAndStatusOrderByEndDateDesc(id, SubscriptionStatus.SUCCESS);
        if(subscriptionEntityList==null|| subscriptionEntityList.isEmpty()){
            userEntity.setPremium(false);
            userRepository.save(userEntity);
            return false;
        }
        SubscriptionEntity newestSubscription = subscriptionEntityList.get(0);
        Date endDate = newestSubscription.getEndDate();
        Date now = new Date();
        if(endDate.before(now)){
            userEntity.setPremium(false);
            userRepository.save(userEntity);
            return false;
        }
        return true;
    }
}
