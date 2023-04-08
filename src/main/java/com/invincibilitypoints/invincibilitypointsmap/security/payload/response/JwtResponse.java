package com.invincibilitypoints.invincibilitypointsmap.security.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class JwtResponse {
	private String accessToken;
    private int expiresIn;
	private String type = "Bearer";
	private String refreshToken;
	private Long id;
	private String username;
	private String email;
	private List<String> roles;

	public JwtResponse(String accessToken, int expiresIn, String refreshToken, Long id, String username, String email, List<String> roles) {
		this.accessToken = accessToken;
        this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
}
