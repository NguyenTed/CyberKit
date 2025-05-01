package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.request.SubscriptionTypeDTO;
import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
import com.cyberkit.cyberkit_server.service.SubscriptionTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionTypeService subscriptionTypeService;

    public SubscriptionController(SubscriptionService subscriptionService, SubscriptionTypeService subscriptionTypeService) {
        this.subscriptionService = subscriptionService;
        this.subscriptionTypeService = subscriptionTypeService;
    }

    @PostMapping
    public ResponseEntity<RestResponse> updateSubscription(@RequestBody VNPayOrderDTO vnPayOrderDTO){
        subscriptionService.updateSubscription(vnPayOrderDTO);
        if(vnPayOrderDTO.getTransactionStatus().equals("00")){
            return ResponseEntity.ok().body(new RestResponse(200,"","","0"));
        }
        return ResponseEntity.ok().body(new RestResponse(401,"","Transaction failed!! Please check the credit card!!","1"));
    }

    @GetMapping("/types")
    public  ResponseEntity<RestResponse> getAllSubscriptionTypes(){
        return ResponseEntity.ok().body(new RestResponse(200,"","",subscriptionTypeService.findAll()));
    }
    @PutMapping("/types")
    public  ResponseEntity<RestResponse> updateSubscriptionType(@RequestBody SubscriptionTypeDTO subscriptionTypeDTO){
        subscriptionTypeService.updateSubscriptionType(subscriptionTypeDTO);
        return ResponseEntity.ok().body(new RestResponse(200,"","",subscriptionTypeService.findAll()));
    }
}
