package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.*;
import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.SubscriptionTypeRepository;
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
    private final UserService userService;
    private final UserRepository userRepository;
    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, AccountService accountService, UserService userService, UserRepository userRepository, SubscriptionTypeRepository subscriptionTypeRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.accountService = accountService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }


    @Override
    public Long createSubscription(Long subscriptionTypeId) {
        if(userService.checkExistingSubscription())
            throw new GeneralAllException("Existing unexpired premium plan!!");
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.findById(subscriptionTypeId).get();
        if(subscriptionTypeEntity==null)
            throw  new GeneralAllException("Invalid subcription type id!!");

        subscriptionEntity.setSubscriptionType(subscriptionTypeEntity);
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
            userEntity.setEndDate(endDate);
            userEntity.setPlanType(subscriptionEntity.getSubscriptionType().getName());
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
