package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.*;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ToolRepository toolRepository;
    private final AccountRepository accountRepository;
    private final ToolMapper toolMapper;

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
    public void addToolToFavoriteTool(String toolId) {
        System.out.println("Add to favorites: " + toolId);
        updateFavouriteTool(toolId, true);
    }

    @Transactional
    @Override
    public void removeToolFromFavoriteTool(String toolId) {
        System.out.println("Remove from favorites: " + toolId);
        updateFavouriteTool(toolId, false);
    }

    private void updateFavouriteTool(String toolId, boolean shouldAdd) {
        UUID toolUUID = UUID.fromString(toolId);
        ToolEntity toolEntity = toolRepository.findById(toolUUID)
                .orElseThrow(() -> new GeneralAllException("Invalid toolId!"));

        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new GeneralAllException("User not authenticated!"));

        AccountEntity accountEntity = accountRepository.findByEmail(email);
        UserEntity userEntity = (UserEntity) accountEntity.getUser();

        if (userEntity == null)
            throw new GeneralAllException("Invalid user!");

        if (!userRepository.existsByIdAndFavouriteToolsId(userEntity.getId(), toolUUID)) {
            if (shouldAdd) {
                userEntity.getFavouriteTools().add(toolEntity);
            } else {
                throw new GeneralAllException("Tool is not in favorites!");
            }
        } else if (userRepository.existsByIdAndFavouriteToolsId(userEntity.getId(), toolUUID)) {
            if (!shouldAdd) {
                userEntity.getFavouriteTools().remove(toolEntity);
            } else {
                throw new GeneralAllException("Tool is already in favorites!");
            }
        }

        userRepository.save(userEntity);
    }

    @Override
    public List<ToolResponse> getMyFavoriteTools() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new GeneralAllException("User not authenticated!"));

        AccountEntity accountEntity = accountRepository.findByEmail(email);
        UserEntity userEntity = (UserEntity) accountEntity.getUser();

        if (userEntity == null)
            throw new GeneralAllException("Invalid user!");

        Set<ToolEntity> toolEntities = userEntity.getFavouriteTools();
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
            throw new UnauthorizedPermissionException("Not applying for Admin!!");
        }
        return false;
    }
}
