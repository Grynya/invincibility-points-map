package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.TokenRefreshRequest;
import com.invincibilitypoints.invincibilitypointsmap.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping
public class AuthController {
    private final AuthService authService;
    @Value("${jwt_expiration_ms}")
    int jwtExpirationMs;

    @Value("${frontend.endpoint}")
    String frontendEndpoint;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUserWithCredentials(loginRequest);
    }

    @GetMapping("/public/registrationConfirm")
    public RedirectView confirmRegistration(@RequestParam final String token) {
        final TokenVerificationResponse result = authService.validateVerificationToken(token);
        if (result.eTokenVerificationStatus().equals(ETokenVerificationStatus.TOKEN_VALID)) {
            return new RedirectView( frontendEndpoint + "/successVerification" +
                    "?accessToken=" + result.jwtResponse().getAccessToken()+
                    "&refreshToken=" + result.jwtResponse().getRefreshToken()+
                    "&expiresIn=" + result.jwtResponse().getExpiresIn());
        }
        return new RedirectView(frontendEndpoint + "/errorVerification?errorMessage=" + result.eTokenVerificationStatus());
    }
    @PostMapping("/public/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request);
    }
    @GetMapping("/user/info-by-access-token")
    public ResponseEntity<?> getUserInfoByAccessToken(@Valid @RequestParam String accessToken) {
        return authService.getUserInfoByAccessToken(accessToken);
    }
    @GetMapping("/public/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(@RequestParam final String token) {
        return authService.isLoggedIn(token);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return authService.logout();
    }
}