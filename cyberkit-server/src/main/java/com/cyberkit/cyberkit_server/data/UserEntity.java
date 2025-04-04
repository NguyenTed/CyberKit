package com.cyberkit.cyberkit_server.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
public class UserEntity extends AbstractUserEntity {
    private boolean isPremium;
    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SubscriptionEntity> subscriptions;
    private Date endDate;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favourite_tool",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tool_id", nullable = false))
    private List<ToolEntity> tools = new ArrayList<>();
}
