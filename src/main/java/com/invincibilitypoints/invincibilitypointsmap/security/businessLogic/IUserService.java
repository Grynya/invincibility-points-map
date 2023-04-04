package com.invincibilitypoints.invincibilitypointsmap.security.businessLogic;
import com.invincibilitypoints.invincibilitypointsmap.security.dto.LoginDto;
import com.invincibilitypoints.invincibilitypointsmap.security.dto.RegisterDto;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import org.springframework.http.ResponseEntity;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;

public interface IUserService {
    String authenticate(LoginDto loginDto);
    ResponseEntity<?> register (RegisterDto registerDto);
    Role saveRole(Role role);

    User saverUser (User user) ;
}
