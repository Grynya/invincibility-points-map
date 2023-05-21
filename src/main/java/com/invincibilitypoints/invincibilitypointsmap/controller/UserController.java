package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.UserDetailsImpl;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping
    public ResponseEntity<?> getPoints(@Valid @RequestParam Long id) {
        return userService.getPoints(id);
    }

    @GetMapping("/user/info-by-access-token")
    public ResponseEntity<?> getUserInfoByAccessToken(@Valid @RequestParam String accessToken) {
        return userService.getUserInfoByAccessToken(accessToken);
    }
    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/likedPoints")
    public ResponseEntity<?> getLikedPoints(@Valid @RequestParam Long userId) {
        return userService.getLikedPoints(userId);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
