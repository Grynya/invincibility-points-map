package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
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

    @GetMapping("/info-by-access-token")
    public ResponseEntity<?> getUserInfoByAccessToken(@Valid @RequestParam String accessToken) {
        return userService.getUserInfoByAccessToken(accessToken);
    }

    @GetMapping("/likedPoints")
    public ResponseEntity<?> getLikedPoints(@Valid @RequestParam Long userId) {
        return userService.getLikedPoints(userId);
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

    @GetMapping("passwordRecovery/update")
    public ResponseEntity<?> updatePasswordRecovery(@Valid @RequestParam String userEmail,
                                                    @Valid @RequestParam String code,
                                                    @Valid @RequestParam String password) {
        return userService.updatePasswordRecovery(userEmail, code, password);
    }
}
