package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;

public interface SubscriptionService {
    public Long createSubscription(String subscriptionType);
    public void updateSubscription(VNPayOrderDTO vnPayOrderDTO);
}
