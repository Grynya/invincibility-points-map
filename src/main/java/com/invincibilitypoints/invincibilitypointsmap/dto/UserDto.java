package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;

public record UserDto(String name, String surname, String email, EStatus userStatus, boolean isAdmin) {
}
