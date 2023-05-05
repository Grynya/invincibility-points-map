package com.invincibilitypoints.invincibilitypointsmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvincibilityPointsMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvincibilityPointsMapApplication.class, args);
    }

//    @Bean
//    CommandLineRunner run(UserService userService, RoleRepository roleRepository) {
//        return args -> {
//            if (roleRepository.findByName(ROLE_ADMIN).isEmpty())
//                roleRepository.save(new Role(ROLE_ADMIN));
//            if (roleRepository.findByName(ROLE_USER).isEmpty())
//                roleRepository.save(new Role(ERole.ROLE_USER));
//
//            userService.createUser(SignupRequest.builder()
//                    .name("name")
//                    .surname("surname")
//                    .email("admin@gmail.com")
//                    .password("admin")
//                    .roles(Set.of("admin", "user"))
//                    .build());
//        };
//    }
}