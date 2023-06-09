package com.invincibilitypoints.invincibilitypointsmap.security.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class JwtResponse {
	private String accessToken;
    private int expiresIn;
	private String tokenType = "Bearer";
	private String refreshToken;
	private Long id;
	private String name;
    private String surname;
    private String email;
    private Set<String> roles;
    public JwtResponse(String accessToken, int expiresIn, String refreshToken, Long id,
                       String name, String surname, String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.roles = roles;
    }
}
