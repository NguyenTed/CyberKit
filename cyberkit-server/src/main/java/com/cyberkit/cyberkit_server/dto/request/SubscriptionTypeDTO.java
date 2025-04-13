package com.cyberkit.cyberkit_server.dto.request;

import jakarta.validation.Valid;
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
@Valid
public class SubscriptionTypeDTO {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    private long price;
    @NotNull
    private int duration;
}
