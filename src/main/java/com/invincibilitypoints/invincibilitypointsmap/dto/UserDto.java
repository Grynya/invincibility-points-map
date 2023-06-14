package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDto(Long id, String name, String surname, String email, EStatus userStatus, Set<String> roles) {

    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getUserStatus(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
    }
}
