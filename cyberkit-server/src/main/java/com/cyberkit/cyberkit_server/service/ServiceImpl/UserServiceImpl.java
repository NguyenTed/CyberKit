package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.*;
import com.cyberkit.cyberkit_server.dto.UserDTO;
import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.exception.UnauthorizedPermissionException;
import com.cyberkit.cyberkit_server.mapper.ToolMapper;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.ToolRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import com.cyberkit.cyberkit_server.service.UserService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ToolRepository toolRepository;
    private final AccountRepository accountRepository;
    private final ToolMapper toolMapper;
    public UserServiceImpl(UserRepository userRepository, SubscriptionRepository subscriptionRepository, ToolRepository toolRepository, AccountRepository accountRepository, ToolMapper toolMapper) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.toolRepository = toolRepository;
        this.accountRepository = accountRepository;
        this.toolMapper = toolMapper;
    }


    @Override
    public Boolean checkValidSubscription(Date endDate, Long userId) {
//        UserEntity userEntity = userRepository.findById(id).get();
//        List<SubscriptionEntity> subscriptionEntityList=  subscriptionRepository.findByUser_IdAndStatusOrderByEndDateDesc(id, SubscriptionStatus.SUCCESS);
//        if(subscriptionEntityList==null|| subscriptionEntityList.isEmpty()){
//            userEntity.setPremium(false);
//            userRepository.save(userEntity);
//            return false;
//        }
//        SubscriptionEntity newestSubscription = subscriptionEntityList.get(0);
//        Date endDate = newestSubscription.getEndDate();
        Date now = new Date();
        if(endDate.before(now)){
            //Update user canceling premium
            UserEntity userEntity = userRepository.findById(userId).get();
            if(userEntity ==null) throw new GeneralAllException("Invalid user id!!");
            userEntity.setPremium(false);
            userEntity.setEndDate(null);
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void likeTool(String toolId) {
        Optional<ToolEntity> optionalToolEntity = toolRepository.findById(UUID.fromString(toolId));
        if(optionalToolEntity.isEmpty()) throw  new GeneralAllException("Invalid toolId!");
        ToolEntity toolEntity = optionalToolEntity.get();
        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        UserEntity userEntity = null;
        if(accountEntity.getUser() instanceof UserEntity){
            userEntity =(UserEntity) accountEntity.getUser();
        }
        if(userEntity==null) throw  new GeneralAllException("Invalid Email!");
        if(!userRepository.existsByIdAndToolsId(userEntity.getId(),UUID.fromString(toolId))){
            List<ToolEntity> toolEntities = userEntity.getTools();
            toolEntities.add(toolEntity);
            userRepository.save(userEntity);
        }
        else{
            userRepository.removeToolFromFavoritesNative(userEntity.getId(),UUID.fromString(toolId));
        }

    }


    @Override
    public List<ToolResponse> getFavouriteTools() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        UserEntity userEntity = null;
        if(accountEntity.getUser() instanceof UserEntity){
            userEntity =(UserEntity) accountEntity.getUser();
        }
        if(userEntity==null) throw  new GeneralAllException("Invalid Email!");
        List<ToolEntity> toolEntities = userEntity.getTools();
        return toolEntities.stream().map(toolMapper::toToolResponse).toList();

    }

    @Override
    public Boolean checkExistingSubscription() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()==true?
                SecurityUtil.getCurrentUserLogin().get():"";
        AccountEntity accountEntity = accountRepository.findByEmail(email);
        AbstractUserEntity abstractUserEntity = accountEntity.getUser();
        if(abstractUserEntity instanceof UserEntity){
            List<SubscriptionEntity> subscriptionEntityList=  subscriptionRepository.findByUser_IdAndStatusOrderByEndDateDesc(abstractUserEntity.getId(), SubscriptionStatus.SUCCESS);
            if(subscriptionEntityList.isEmpty()) return false;
            SubscriptionEntity newestSubscription = subscriptionEntityList.get(0);
            Date now = new Date();
            Date endDate = newestSubscription.getEndDate();
            if(endDate.after(now)) return true;
        }
        else{
            throw  new UnauthorizedPermissionException("Not applying for Admin!!");
        }
        return false;
    }
}
