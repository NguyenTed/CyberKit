package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.SubscriptionEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.enums.SubscriptionType;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AccountService accountService;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, AccountService accountService) {
        this.subscriptionRepository = subscriptionRepository;
        this.accountService = accountService;
    }


    @Override
    public Long createSubscription(String subscriptionType) {
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriptionType(SubscriptionType.valueOf(subscriptionType));
        subscriptionEntity.setStatus(SubscriptionStatus.PENDING);

        String userEmail = SecurityUtil.getCurrentUserLogin().get();
        AbstractUserEntity abstractUserEntity = accountService.getUserByEmail(userEmail);
        if(abstractUserEntity instanceof UserEntity){
            subscriptionEntity.setUser( (UserEntity) abstractUserEntity);
            subscriptionRepository.save(subscriptionEntity);
        }
        else{
            throw new GeneralAllException("This function is only for user!!");
        }
        return subscriptionEntity.getId();
    }
}
