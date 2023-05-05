package com.invincibilitypoints.invincibilitypointsmap.security.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class SignupRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String surname;
 
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
