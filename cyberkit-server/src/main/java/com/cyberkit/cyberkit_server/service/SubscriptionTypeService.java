package com.cyberkit.cyberkit_server.service;

import java.util.List;

import com.cyberkit.cyberkit_server.dto.request.SubscriptionTypeDTO;

public interface SubscriptionTypeService {
    public List<SubscriptionTypeDTO> findAll();

    public void updateSubscriptionType(SubscriptionTypeDTO subscriptionTypeDTO);

    public Long getPriceFromSubscriptionType(Long subscriptionTypeId);
}
