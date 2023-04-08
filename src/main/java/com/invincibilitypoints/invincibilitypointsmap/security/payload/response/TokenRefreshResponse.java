package com.invincibilitypoints.invincibilitypointsmap.security.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

}
