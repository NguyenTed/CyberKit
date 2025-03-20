package com.cyberkit.cyberkit_server.config;

import com.cyberkit.cyberkit_server.data.*;
import com.cyberkit.cyberkit_server.enums.GenderEnum;
import com.cyberkit.cyberkit_server.enums.RoleEnum;
import com.cyberkit.cyberkit_server.repository.AccountRepository;
import com.cyberkit.cyberkit_server.repository.AdminRepository;
import com.cyberkit.cyberkit_server.repository.SubscriptionRepository;
import com.cyberkit.cyberkit_server.repository.UserRepository;
import jakarta.persistence.*;
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
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitialize(AccountRepository accountRepository, AdminRepository adminRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
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
            secondUser.setPremium(true);
            secondUser.setSubscriptions(subscriptionEntityList);
            //Save the second user
            secondUser=userRepository.save(secondUser);
            //Save the second user Account
            AccountEntity secondUserAccount = new AccountEntity(null,"user2@gmail.com", passwordEncoder.encode("123456"),"", RoleEnum.USER ,secondUser);
            accountRepository.save(secondUserAccount);
        }
        if(countAccounts>0){
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        }
        else{
            System.out.println(">>> END INIT DATABASE");
        }

    }
}
