package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.AbstractUserEntity;
import com.cyberkit.cyberkit_server.data.AccountEntity;
import com.cyberkit.cyberkit_server.data.SubscriptionEntity;
import com.cyberkit.cyberkit_server.data.UserEntity;
import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.enums.SubscriptionType;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
import com.cyberkit.cyberkit_server.service.UserService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AccountService accountService;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, AccountService accountService, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.accountService = accountService;
        this.userRepository = userRepository;
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

    @Override
    public void updateSubscription(VNPayOrderDTO vnPayOrderDTO) {
        // handle success transaction
        SubscriptionEntity subscriptionEntity = subscriptionRepository.findById(vnPayOrderDTO.getSubscriptionId()).get();
        if (subscriptionEntity==null){
            throw new GeneralAllException("Invalid Subscription!!");
        }
        if(vnPayOrderDTO.getTransactionStatus().equals("00")){
            subscriptionEntity.setCreatedDate(vnPayOrderDTO.getPayDate());
            subscriptionEntity.setStatus(SubscriptionStatus.SUCCESS);
            // Set expire based on Subscription type.
            Date payDate = vnPayOrderDTO.getPayDate();
            LocalDateTime payDateTime = payDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endDateTime = payDateTime.plusDays(subscriptionEntity.getSubscriptionType().getDuration());
            Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
            subscriptionEntity.setEndDate(endDate);
            subscriptionEntity.setTransactionNo(vnPayOrderDTO.getTransactionNo());
            // Save the subscription
            subscriptionRepository.save(subscriptionEntity);
            // Set premium true on user
            UserEntity userEntity = subscriptionEntity.getUser();
            userEntity.setPremium(true);
            // Save the user
            userRepository.save(userEntity);
        }
        else{
            // With failing in transaction only update status and transactionNo
            subscriptionEntity.setStatus(SubscriptionStatus.FAIL);
            subscriptionEntity.setTransactionNo(vnPayOrderDTO.getTransactionNo());
        }
    }
}
