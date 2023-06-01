package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.dto.UserDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERole;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.events.OnPasswordRecoveryEvent;
import com.invincibilitypoints.invincibilitypointsmap.events.OnRegistrationCompleteEvent;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.model.RefreshToken;
import com.invincibilitypoints.invincibilitypointsmap.security.model.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.model.VerificationToken;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.JwtResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.VerificationTokenRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.security.jwt.JwtUtils;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final HttpServletRequest request;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));
    @Value("${jwt_expiration_ms}")
    int jwtExpirationMs;

    @Autowired
    public UserService(AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       ApplicationEventPublisher eventPublisher,
                       VerificationTokenRepository verificationTokenRepository,
                       RefreshTokenService refreshTokenService, HttpServletRequest request) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.eventPublisher = eventPublisher;
        this.verificationTokenRepository = verificationTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.request = request;
    }

    public ResponseEntity<?> authenticateUserWithCredentials(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getUserStatus().equals(EStatus.INACTIVE)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(errors.getString("inactive_user")));
        }

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), userDetails.getId(),
                userDetails.getName(), userDetails.getSurname(), userDetails.getEmail(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())));
    }

    public JwtResponse generateTokens(User user) {

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), user.getId(),
                user.getEmail(), user.getSurname(), user.getEmail(),
                user
                        .getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()));
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
//        String appUrl = request.getHeader("Host");
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userRepository.findByEmail("hrynenko.anastasia@gmail.com").get(),
//                request.getLocale(), appUrl));
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

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    public TokenVerificationResponse validateVerificationToken(String token) {
        final Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
        if (verificationTokenOptional.isPresent()) {
            VerificationToken verificationToken = verificationTokenOptional.get();

            final User user = verificationToken.getUser();
            final Calendar cal = Calendar.getInstance();
            if ((verificationToken.getExpiryDate()
                    .getTime() - cal.getTime()
                    .getTime()) <= 0) {
                verificationTokenRepository.delete(verificationToken);
                return new TokenVerificationResponse(ETokenVerificationStatus.TOKEN_EXPIRED, null);
            }

            user.setUserStatus(EStatus.ACTIVE);
            verificationTokenRepository.delete(verificationToken);
            userRepository.save(user);
            JwtResponse responseEntity = generateTokens(user);
            return new TokenVerificationResponse(ETokenVerificationStatus.TOKEN_VALID, responseEntity);
        }
        return new TokenVerificationResponse(ETokenVerificationStatus.TOKEN_INVALID, null);
    }

    public ResponseEntity<?> getUserInfoByAccessToken(String accessToken) {
        String username = jwtUtils.getEmailFromJwtToken(accessToken);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_id")));
        return ResponseEntity.ok(UserDto.fromUser(user.get()));
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
        System.out.println("service");
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

    public ResponseEntity<?> logout(){
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            Long userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
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
