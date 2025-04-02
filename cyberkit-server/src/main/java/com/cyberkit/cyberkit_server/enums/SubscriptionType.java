package com.cyberkit.cyberkit_server.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SubscriptionType {
    BASIC(100000, 30),
    MEDIUM(200000, 90),
    ADVANCE(500000, 365);

    private final long price;
    private final int duration;

    // Constructor
    SubscriptionType(long price, int duration) {
        this.price = price;
        this.duration = duration;
    }
}