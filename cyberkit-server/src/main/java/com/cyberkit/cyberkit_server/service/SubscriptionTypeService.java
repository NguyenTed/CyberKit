package com.cyberkit.cyberkit_server.service;

import com.cyberkit.cyberkit_server.dto.request.SubscriptionTypeDTO;

import java.util.List;

public interface SubscriptionTypeService {
    public List<SubscriptionTypeDTO> findAll();
    public void updateSubscriptionType(SubscriptionTypeDTO subscriptionTypeDTO);
    public Long getPriceFromSubscriptionType(Long subscriptionTypeId);
}
