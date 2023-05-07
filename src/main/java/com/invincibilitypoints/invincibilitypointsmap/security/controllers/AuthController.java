package com.invincibilitypoints.invincibilitypointsmap.security.controllers;

import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.TokenRefreshRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.UserDetailsImpl;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt_expiration_ms}")
    int jwtExpirationMs;

    @Autowired
    public AuthController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.authenticateUserWithCredentials(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
        return userService.registration(signUpRequest, request);
    }

    @GetMapping("/registrationConfirm")
    public RedirectView confirmRegistration(@RequestParam("token") final String token) {
        final TokenVerificationResponse result = userService.validateVerificationToken(token);
        String url = "http://localhost:3000";
        if (result.eTokenVerificationStatus().equals(ETokenVerificationStatus.TOKEN_VALID)) {
            return new RedirectView( url + "/successVerification" +
                    "?accessToken=" + result.jwtResponse().getAccessToken()+
                    "&refreshToken=" + result.jwtResponse().getRefreshToken()+
                    "&expiresIn=" + result.jwtResponse().getExpiresIn());
        }
        return new RedirectView(url + "/errorVerification?errorMessage=" + result.eTokenVerificationStatus());
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.refreshToken(request);
    }
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            System.out.println(userDetails);
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}