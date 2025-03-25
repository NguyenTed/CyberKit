package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.request.VNPayOrderDTO;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<RestResponse> updateSubscription(@RequestBody VNPayOrderDTO vnPayOrderDTO){
        subscriptionService.updateSubscription(vnPayOrderDTO);
        if(vnPayOrderDTO.getTransactionStatus().equals("00")){
            return ResponseEntity.ok().body(new RestResponse(200,"","","0"));
        }
        return ResponseEntity.ok().body(new RestResponse(401,"","Transaction failed!! Please check the credit card!!","1"));
    }

}
