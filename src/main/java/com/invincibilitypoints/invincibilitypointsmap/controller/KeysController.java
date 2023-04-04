package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.dto.KeysDto;
import com.invincibilitypoints.invincibilitypointsmap.service.KeysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/public/keys")
public class KeysController {
    private final KeysService keysService;
    @Autowired
    public KeysController(KeysService keysService) {
        this.keysService = keysService;
    }
    @GetMapping()
    public KeysDto getKeys() {
        return keysService.getKeys();
    }
}
