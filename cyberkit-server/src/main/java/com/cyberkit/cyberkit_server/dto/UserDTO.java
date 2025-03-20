package com.cyberkit.cyberkit_server.dto;


import com.cyberkit.cyberkit_server.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private boolean isPremium;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
