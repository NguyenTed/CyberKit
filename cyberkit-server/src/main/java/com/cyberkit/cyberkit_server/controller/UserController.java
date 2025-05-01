package com.cyberkit.cyberkit_server.controller;

import com.cyberkit.cyberkit_server.dto.request.AddToFavouriteToolRequest;
import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import com.cyberkit.cyberkit_server.dto.response.ToolResponse;
import com.cyberkit.cyberkit_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/favorites")
    public RestResponse<List<ToolResponse>> getMyFavoriteTools(){
        return RestResponse.<List<ToolResponse>>builder()
                .data(userService.getMyFavoriteTools())
                .message("Get my favorite tools successfully")
                .build();
    }

    @PostMapping("/favorites")
    public RestResponse<Void> addToolToFavorites(@RequestBody AddToFavouriteToolRequest request) {
        userService.addToolToFavoriteTool(request.toolId());
        return RestResponse.<Void>builder().message("Tool added to favourites.").build();
    }

    @DeleteMapping("/favorites/{toolId}")
    public RestResponse<Void> removeToolFromFavorites(@PathVariable String toolId) {
        userService.removeToolFromFavoriteTool(toolId);
        return RestResponse.<Void>builder().message("Tool removed from favourites.").build();
    }
}
