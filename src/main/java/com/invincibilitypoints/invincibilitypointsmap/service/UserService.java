package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.PointDto;
import com.invincibilitypoints.invincibilitypointsmap.model.Point;
import com.invincibilitypoints.invincibilitypointsmap.model.UserStatus;
import com.invincibilitypoints.invincibilitypointsmap.security.models.ERole;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }
    @Transactional
    public ResponseEntity<?> createUser(SignupRequest signUpRequest){

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = User
                .builder()
                .name(signUpRequest.getName())
                .surname(signUpRequest.getSurname())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .points(Collections.emptySet())
                .likedPoints(Collections.emptySet())
                .userStatus(UserStatus.ACTIVE)
                .build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                }
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }
    public ResponseEntity<?> getPoints(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            List<PointDto> pointDtos = new ArrayList<>();
            for (Point point : user.get().getPoints()) {
                PointDto pointDto = PointDto.fromPoint(point);
                pointDtos.add(pointDto);
            }
            return ResponseEntity.ok().body(pointDtos);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
    }
}
