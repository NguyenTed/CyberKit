package com.cyberkit.cyberkit_server.dto.request;

import java.util.Date;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.cyberkit.cyberkit_server.enums.GenderEnum;
import com.cyberkit.cyberkit_server.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotNull
    @NotBlank(message = "Name is not blank!")
    private String name;

    @NotNull
    @NotBlank(message = "Password is not blank!")
    private String password;

    @Email
    @NotBlank(message = "Email is not blank!")
    private String email;

    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
