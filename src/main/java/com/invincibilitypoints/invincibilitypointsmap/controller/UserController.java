package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        return userService.logout();
    }
}
