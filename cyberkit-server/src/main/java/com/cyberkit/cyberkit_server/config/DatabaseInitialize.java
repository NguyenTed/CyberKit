package com.cyberkit.cyberkit_server.config;

import com.cyberkit.cyberkit_server.data.*;
import com.cyberkit.cyberkit_server.enums.GenderEnum;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DatabaseInitialize implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ToolCategoryRepository toolCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public DatabaseInitialize(AccountRepository accountRepository, AdminRepository adminRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository, ToolCategoryRepository toolCategoryRepository, PasswordEncoder passwordEncoder, SubscriptionTypeRepository subscriptionTypeRepository) {
        this.accountRepository = accountRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.toolCategoryRepository = toolCategoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        seedToolCategory();
        long countAccounts = this.accountRepository.count();
        if(countAccounts==0){
            LocalDate localDate = LocalDate.of(2000, 1, 1);
            Date dateOfBirth = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            // Init the admin entity
            AdminEntity admin = new AdminEntity();
            admin.setId(null);
            admin.setName("Admin");
            admin.setDateOfBirth(dateOfBirth);
            admin.setGender(GenderEnum.MALE);
            //Save the admin
            admin=adminRepository.save(admin);
            // Save the admin Account
            AccountEntity adminAccount = new AccountEntity(null,"admin@gmail.com", passwordEncoder.encode("123456"),"", RoleEnum.ADMIN ,admin);
            accountRepository.save(adminAccount);

            // Init the first user entity
            UserEntity firstUser = new UserEntity();
            firstUser.setId(null);
            firstUser.setName("User1");
            firstUser.setDateOfBirth(dateOfBirth);
            firstUser.setGender(GenderEnum.MALE);
            firstUser.setPremium(false);
            //Save the first user
            firstUser=userRepository.save(firstUser);
            //Save the first user Account
            AccountEntity firstUserAccount = new AccountEntity(null,"user1@gmail.com", passwordEncoder.encode("123456"),"", RoleEnum.USER ,firstUser);
            accountRepository.save(firstUserAccount);

            //Init the subscription for second user
            SubscriptionEntity subscription = new SubscriptionEntity();
            LocalDate expiredLocalDate = LocalDate.of(2025, 05, 05);
            Date expiredDate = Date.from(expiredLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            subscription.setId(null);
            subscription.setEndDate(expiredDate);
            subscription = subscriptionRepository.save(subscription);
            List<SubscriptionEntity> subscriptionEntityList = new ArrayList<>();
            subscriptionEntityList.add(subscription);

            // Init the second user entity
            UserEntity secondUser = new UserEntity();
            secondUser.setId(null);
            secondUser.setName("User2");
            secondUser.setDateOfBirth(dateOfBirth);
            secondUser.setGender(GenderEnum.MALE);
            secondUser.setPremium(false);
            secondUser.setSubscriptions(subscriptionEntityList);
            //Save the second user
            secondUser=userRepository.save(secondUser);
            //Save the second user Account
            AccountEntity secondUserAccount = new AccountEntity(null,"user2@gmail.com", passwordEncoder.encode("123456"),"", RoleEnum.USER ,secondUser);
            accountRepository.save(secondUserAccount);

            //Init the subscription entity
            SubscriptionTypeEntity subscriptionTypeEntity1 = new SubscriptionTypeEntity();
            subscriptionTypeEntity1.setName("BASIC");
            subscriptionTypeEntity1.setDuration(30);
            subscriptionTypeEntity1.setPrice(100000);

            SubscriptionTypeEntity subscriptionTypeEntity2 = new SubscriptionTypeEntity();
            subscriptionTypeEntity2.setName("MEDIUM");
            subscriptionTypeEntity2.setDuration(90);
            subscriptionTypeEntity2.setPrice(200000);

            SubscriptionTypeEntity subscriptionTypeEntity3 = new SubscriptionTypeEntity();
            subscriptionTypeEntity3.setName("ADVANCE");
            subscriptionTypeEntity3.setDuration(365);
            subscriptionTypeEntity3.setPrice(500000);
            subscriptionTypeRepository.save(subscriptionTypeEntity1);
            subscriptionTypeRepository.save(subscriptionTypeEntity2);
            subscriptionTypeRepository.save(subscriptionTypeEntity3);
        }
        if(countAccounts>0){
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        }
        else{
            System.out.println(">>> END INIT DATABASE");
        }

    }

    private void seedToolCategory() {
        if (toolCategoryRepository.count() > 0) {
            System.out.println("Already have tool categories data");
            return;
        }

        List<ToolCategoryEntity> catList = new ArrayList<>();
        catList.add(ToolCategoryEntity.builder().name("Crypto").icon("SiCryptpad").build());
        catList.add(ToolCategoryEntity.builder().name("Converter").icon("SiConvertio").build());
        catList.add(ToolCategoryEntity.builder().name("Web").icon("CgBrowser").build());
        catList.add(ToolCategoryEntity.builder().name("Images and Videos").icon("FaImage").build());
        catList.add(ToolCategoryEntity.builder().name("Development").icon("FaLaptopCode").build());
        catList.add(ToolCategoryEntity.builder().name("Network").icon("FaNetworkWired").build());
        catList.add(ToolCategoryEntity.builder().name("Math").icon("BiMath").build());
        catList.add(ToolCategoryEntity.builder().name("Measurement").icon("FaRulerCombined").build());
        catList.add(ToolCategoryEntity.builder().name("Text").icon("IoText").build());
        catList.add(ToolCategoryEntity.builder().name("Data").icon("FaDatabase").build());

        toolCategoryRepository.saveAll(catList);

        System.out.println("Finish seeding tool categories data");
    }
}
