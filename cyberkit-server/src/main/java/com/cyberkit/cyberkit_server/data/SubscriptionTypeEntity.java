package com.cyberkit.cyberkit_server.data;

import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription_types")
public class SubscriptionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long price;
    private int duration;

    @OneToMany(mappedBy = "subscriptionType", fetch = FetchType.LAZY)
    private List<SubscriptionEntity> subscriptions;
}
