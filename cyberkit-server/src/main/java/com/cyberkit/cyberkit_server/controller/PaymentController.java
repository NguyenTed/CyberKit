package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
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

    public PaymentController(VNPayService vnPayService, SubscriptionService subscriptionService) {
        this.vnPayService = vnPayService;
        this.subscriptionService = subscriptionService;
    }
    @GetMapping("/vnpay/url/{type}")
    public ResponseEntity<RestResponse> getVNPayUrl(@PathVariable("type") String subscriptionType){
        Long createdSubscriptionId= subscriptionService.createSubscription(subscriptionType);
        String VNPayUrl = vnPayService.generatePaymentUrl(subscriptionType,createdSubscriptionId);
        return ResponseEntity.ok(new RestResponse<>(200,"","",VNPayUrl));
    }

}
