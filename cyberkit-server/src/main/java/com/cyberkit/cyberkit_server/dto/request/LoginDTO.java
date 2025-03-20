package com.cyberkit.cyberkit_server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotNull
    @Email
    @NotBlank(message = "Email is not blank!")
    private String email;
    @NotNull
    @NotBlank(message = "Password is not blank!")
    private String password;
}
