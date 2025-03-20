package com.cyberkit.cyberkit_server.dto.response;

import com.cyberkit.cyberkit_server.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResLoginDTO {
    private UserDTO user;
    private String accessToken;
}

