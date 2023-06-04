package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.registration(signUpRequest);
    }
    @GetMapping("/public/passwordRecovery/sendEmail")
    public ResponseEntity<?> sendEmailPasswordRecovery(@Valid @RequestParam String userEmail,
                                                       HttpServletRequest request) {
        return userService.sendEmailPasswordRecovery(userEmail, request);
    }

    @GetMapping("/public/passwordRecovery/checkCode")
    public ResponseEntity<?> checkCodePasswordRecovery(@Valid @RequestParam String userEmail,
                                                       @Valid @RequestParam String code) {
        return userService.checkCodePasswordRecovery(userEmail, code);
    }

    @GetMapping("/public/passwordRecovery/update")
    public ResponseEntity<?> updatePasswordRecovery(@Valid @RequestParam String userEmail,
                                                    @Valid @RequestParam String code,
                                                    @Valid @RequestParam String password) {
        return userService.updatePasswordRecovery(userEmail, code, password);
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
}
