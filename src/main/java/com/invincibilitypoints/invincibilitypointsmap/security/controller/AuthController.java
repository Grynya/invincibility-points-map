package com.invincibilitypoints.invincibilitypointsmap.security.controller;

import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.TokenRefreshRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.security.jwt.JwtUtils;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/public")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    @Value("${jwt_expiration_ms}")
    int jwtExpirationMs;

    @Value("${frontend.endpoint}")
    String frontendEndpoint;
    @Autowired
    public AuthController(UserService userService, RefreshTokenService refreshTokenService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.authenticateUserWithCredentials(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.registration(signUpRequest);
    }

    @GetMapping("/registrationConfirm")
    public RedirectView confirmRegistration(@RequestParam final String token) {
        final TokenVerificationResponse result = userService.validateVerificationToken(token);
        if (result.eTokenVerificationStatus().equals(ETokenVerificationStatus.TOKEN_VALID)) {
            return new RedirectView( frontendEndpoint + "/successVerification" +
                    "?accessToken=" + result.jwtResponse().getAccessToken()+
                    "&refreshToken=" + result.jwtResponse().getRefreshToken()+
                    "&expiresIn=" + result.jwtResponse().getExpiresIn());
        }
        return new RedirectView(frontendEndpoint + "/errorVerification?errorMessage=" + result.eTokenVerificationStatus());
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.refreshToken(request);
    }

    @GetMapping("/passwordRecovery/sendEmail")
    public ResponseEntity<?> sendEmailPasswordRecovery(@Valid @RequestParam String userEmail,
                                                       HttpServletRequest request) {
        return userService.sendEmailPasswordRecovery(userEmail, request);
    }

    @GetMapping("/passwordRecovery/checkCode")
    public ResponseEntity<?> checkCodePasswordRecovery(@Valid @RequestParam String userEmail,
                                                       @Valid @RequestParam String code) {
        return userService.checkCodePasswordRecovery(userEmail, code);
    }

    @GetMapping("/passwordRecovery/update")
    public ResponseEntity<?> updatePasswordRecovery(@Valid @RequestParam String userEmail,
                                                    @Valid @RequestParam String code,
                                                    @Valid @RequestParam String password) {
        return userService.updatePasswordRecovery(userEmail, code, password);
    }

    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(@RequestParam final String token) {
        return jwtUtils.isLoggedIn(token);
    }
}