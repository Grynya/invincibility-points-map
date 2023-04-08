package com.invincibilitypoints.invincibilitypointsmap;

import com.invincibilitypoints.invincibilitypointsmap.model.UserStatus;
import com.invincibilitypoints.invincibilitypointsmap.security.businessLogic.UserService;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.models.RoleName;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import com.invincibilitypoints.invincibilitypointsmap.security.persistance.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.persistance.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootApplication
public class InvincibilityPointsMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvincibilityPointsMapApplication.class, args);
    }

    @Bean
    CommandLineRunner run (UserService userService , RoleRepository roleRepository , UserRepository userRepository , PasswordEncoder passwordEncoder)
    {return  args -> {
        if (roleRepository.findByRoleName(RoleName.USER)==null)
            userService.saveRole(new Role(RoleName.USER));
        if (roleRepository.findByRoleName(RoleName.ADMIN)==null)
            userService.saveRole(new Role(RoleName.ADMIN));
        if (userRepository.findByEmail("admin@gmail.com").isEmpty())
            userService.saverUser(new User(null, "name", "surname", "admin@gmail.com",
                passwordEncoder.encode("adminPassword"),
                new HashSet<>(), UserStatus.ACTIVE, new HashSet<>(), new ArrayList<>(), null,
                    null));

        Role role = roleRepository.findByRoleName(RoleName.ADMIN);
        User user = userRepository.findByEmail("admin@gmail.com").orElse(null);
        user.getRoles().add(role);
        userService.saverUser(user);

    };}
}