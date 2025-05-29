package com.cyberkit.cyberkit_server.data;

import java.util.*;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends AbstractUserEntity {
    private boolean isPremium;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SubscriptionEntity> subscriptions;

    private Date endDate;
    private String planType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favourite_tool",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tool_id", nullable = false))
    private Set<ToolEntity> favouriteTools = new HashSet<>();
}
