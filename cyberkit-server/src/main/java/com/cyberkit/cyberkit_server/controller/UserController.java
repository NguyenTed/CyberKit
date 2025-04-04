package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.service.ToolService;
import com.cyberkit.cyberkit_server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/like/{toolId}")
    public ResponseEntity<RestResponse> likeTool(@PathVariable String toolId){
        userService.likeTool(toolId);
        return ResponseEntity.ok(new RestResponse<>(200,"","",userService.getFavouriteTools()));
    }
    @GetMapping("")
    public ResponseEntity<RestResponse> getFavouriteTools(){
        return ResponseEntity.ok(new RestResponse<>(200,"","",userService.getFavouriteTools()));
    }

}
