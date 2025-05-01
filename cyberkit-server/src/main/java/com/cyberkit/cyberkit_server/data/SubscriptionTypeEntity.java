package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="subscription_types")
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
