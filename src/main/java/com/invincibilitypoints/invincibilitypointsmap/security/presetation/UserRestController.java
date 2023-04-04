package com.invincibilitypoints.invincibilitypointsmap.security.presetation;

import com.invincibilitypoints.invincibilitypointsmap.security.businessLogic.IUserService;
import com.invincibilitypoints.invincibilitypointsmap.security.dto.LoginDto;
import com.invincibilitypoints.invincibilitypointsmap.security.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService iUserService ;

    //RessourceEndPoint:http://localhost:8087/api/user/register
    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDto registerDto)
    { return  iUserService.register(registerDto);}

    //RessourceEndPoint:http://localhost:8087/api/user/authenticate
    @PostMapping("/authenticate")
    public String authenticate(@RequestBody LoginDto loginDto)
    { return  iUserService.authenticate(loginDto);}

}