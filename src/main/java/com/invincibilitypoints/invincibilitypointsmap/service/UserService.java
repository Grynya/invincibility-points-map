package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.dto.UserDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERole;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.events.OnPasswordRecoveryEvent;
import com.invincibilitypoints.invincibilitypointsmap.events.OnRegistrationCompleteEvent;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
import com.invincibilitypoints.invincibilitypointsmap.security.model.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest request;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));
    @Value("${jwt_expiration_ms}")
    int jwtExpirationMs;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       ApplicationEventPublisher eventPublisher,
                       HttpServletRequest request) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.eventPublisher = eventPublisher;
        this.request = request;
    }

    @Transactional
    public User createUser(SignupRequest signUpRequest) {
        User user = User
                .builder()
                .name(signUpRequest.getName())
                .surname(signUpRequest.getSurname())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .points(Collections.emptySet())
                .ratedPoints(Collections.emptySet())
                .userStatus(EStatus.INACTIVE)
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(errors.getString("invalid_user_id")));
        roles.add(userRole);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public ResponseEntity<?> registration(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(errors.getString("email_in_use"));
        }
        User registered = createUser(signUpRequest);
        String appUrl = request.getHeader("Host");
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getPoints(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            List<MapPointDto> pointDtos = new ArrayList<>();
            for (MapPoint point : user.get().getPoints()) {
                MapPointDto pointDto = MapPointDto.fromPoint(point);
                pointDtos.add(pointDto);
            }
            return ResponseEntity.ok().body(pointDtos);
        }
        return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_id")));
    }

    public ResponseEntity<?> getLikedPoints(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return (optionalUser.isPresent()) ?
                ResponseEntity
                        .ok(optionalUser
                                .get()
                                .getRatedPoints()
                                .stream()
                                .filter(ratedPoint -> ratedPoint.getRating() == ERating.LIKED)
                                .map((RatedPoint::getPoint))
                                .map(MapPointDto::fromPoint)
                                .toList()) :
                ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_id")));
    }

    public ResponseEntity<?> sendEmailPasswordRecovery(String userEmail, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_email")));
        }
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnPasswordRecoveryEvent(optionalUser.get(),
                request.getLocale(), appUrl));
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<?> checkCodePasswordRecovery(String userEmail, String code) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_email")));
        }
        try {
            return ResponseEntity.ok().body(isValidCode(optionalUser.get(), code));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_code")));
        }
    }

    public ResponseEntity<?> updatePasswordRecovery(String userEmail, String code, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        System.out.println(userEmail);
        System.out.println(code);
        System.out.println(password);

        if (optionalUser.isPresent()) {
            try {
                User user = optionalUser.get();
                if (isValidCode(user, code)) {
                    user.setPassword(encoder.encode(password));
                    user.setCode(null);
                    userRepository.save(user);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_code")));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_code")));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_email")));
        }
    }

    private boolean isValidCode(User user, String code) {
        int parsedCode = Integer.parseInt(code);
        if (code.length() != 6) {
            throw new NumberFormatException();
        }
        return user.getCode().equals(parsedCode);
    }

    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll().stream().map(UserDto::fromUser));
    }
}
