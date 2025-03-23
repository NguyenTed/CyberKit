package com.cyberkit.cyberkit_server.controlller;

import com.cyberkit.cyberkit_server.dto.request.RegisterDTO;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.AccountService;
import com.cyberkit.cyberkit_server.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping()
    public ResponseEntity<RestResponse> createUser(@RequestBody RegisterDTO registerDTO){
        accountService.createAccount(registerDTO);
        return ResponseEntity.status(200).body(new RestResponse<>( 201,"","Create user successfully!",registerDTO));
    }

}
