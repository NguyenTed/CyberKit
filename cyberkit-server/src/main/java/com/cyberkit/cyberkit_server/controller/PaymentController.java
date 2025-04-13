package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
import com.cyberkit.cyberkit_server.service.SubscriptionTypeService;
import com.cyberkit.cyberkit_server.vnpay.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final VNPayService vnPayService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionTypeService subscriptionTypeService;

    public PaymentController(VNPayService vnPayService, SubscriptionService subscriptionService, SubscriptionTypeService subscriptionTypeService) {
        this.vnPayService = vnPayService;
        this.subscriptionService = subscriptionService;
        this.subscriptionTypeService = subscriptionTypeService;
    }
    @GetMapping("/vnpay/url/{id}")
    public ResponseEntity<RestResponse> getVNPayUrl(@PathVariable("id") Long subscriptionTypeId){
        Long createdSubscriptionId= subscriptionService.createSubscription(subscriptionTypeId);
        String VNPayUrl = vnPayService.generatePaymentUrl(subscriptionTypeService.getPriceFromSubscriptionType(subscriptionTypeId),createdSubscriptionId);
        return ResponseEntity.ok(new RestResponse<>(200,"","",VNPayUrl));
    }
}
