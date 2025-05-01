package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;

public interface SubscriptionService {
    public Long createSubscription(Long subscriptionTypeId);
    public void updateSubscription(VNPayOrderDTO vnPayOrderDTO);
}
