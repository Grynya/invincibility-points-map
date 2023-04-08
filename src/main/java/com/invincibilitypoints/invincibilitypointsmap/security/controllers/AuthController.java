package com.invincibilitypoints.invincibilitypointsmap.security.controllers;

import com.invincibilitypoints.invincibilitypointsmap.security.exception.TokenRefreshException;
import com.invincibilitypoints.invincibilitypointsmap.security.models.RefreshToken;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.TokenRefreshRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.JwtResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.TokenRefreshResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.security.jwt.JwtUtils;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.UserDetailsImpl;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService, JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.createUser(signUpRequest);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
