package com.cyberkit.cyberkit_server.data;

import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;
import com.cyberkit.cyberkit_server.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date endDate;
    @CreationTimestamp
    private Date createdDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Long orderId;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

}
