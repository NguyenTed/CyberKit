package com.cyberkit.cyberkit_server.data;

import java.util.Date;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.cyberkit.cyberkit_server.enums.SubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
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

    private String TransactionNo;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "subscriptionType_id")
    private SubscriptionTypeEntity subscriptionType;
}
