package com.invincibilitypoints.invincibilitypointsmap;

import com.invincibilitypoints.invincibilitypointsmap.security.models.ERole;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

import static com.invincibilitypoints.invincibilitypointsmap.security.models.ERole.ROLE_ADMIN;
import static com.invincibilitypoints.invincibilitypointsmap.security.models.ERole.ROLE_USER;

@SpringBootApplication
public class InvincibilityPointsMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvincibilityPointsMapApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName(ROLE_ADMIN).isEmpty())
                roleRepository.save(new Role(ROLE_ADMIN));
            if (roleRepository.findByName(ROLE_USER).isEmpty())
                roleRepository.save(new Role(ERole.ROLE_USER));

            userService.createUser(SignupRequest.builder()
                    .name("name")
                    .surname("surname")
                    .email("admin@gmail.com")
                    .password("admin")
                    .role(Set.of("admin", "user"))
                    .build());
        };
    }
}