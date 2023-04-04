package com.invincibilitypoints.invincibilitypointsmap.security.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto implements Serializable {

    String name ;
    String surname ;
    String email;
    String password ;
}
