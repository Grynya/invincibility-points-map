package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.ErrorResponse;
import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.dto.UserDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERole;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.events.OnRegistrationCompleteEvent;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.models.RefreshToken;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import com.invincibilitypoints.invincibilitypointsmap.security.models.VerificationToken;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.SignupRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.JwtResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RoleRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.VerificationTokenRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.security.jwt.JwtUtils;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.RefreshTokenService;
import com.invincibilitypoints.invincibilitypointsmap.security.security.services.UserDetailsImpl;
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
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.eventPublisher = eventPublisher;
        this.verificationTokenRepository = verificationTokenRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseEntity<?> authenticateUserWithCredentials(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getUserStatus().equals(EStatus.INACTIVE)) {
            ErrorResponse errorResponse = new ErrorResponse("User is inactive");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getSurname(), userDetails.getEmail(), containsAdminRole(roles)));
    }

    public JwtResponse generateTokens(User user) {

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        List<String> roles = user
                .getRoles()
                .stream()
                .map(Role::getName)
                .map(Object::toString)
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), user.getId(),
                user.getEmail(), user.getSurname(), user.getEmail(), containsAdminRole(roles));
    }

    private boolean containsAdminRole(List<String> roles) {
        return roles.contains(ERole.ROLE_ADMIN.name());
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
                .likedPoints(Collections.emptySet())
                .userStatus(EStatus.INACTIVE)
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public ResponseEntity<?> registration(SignupRequest signUpRequest, HttpServletRequest request) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        User registered = createUser(signUpRequest);
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
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
        return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
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
            return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
        return ResponseEntity.ok(new UserDto(user.get().getId(), user.get().getName(), user.get().getSurname(),
                user.get().getEmail(), user.get().getUserStatus(), false));
    }

    public ResponseEntity<?> getLikedPoints(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return (optionalUser.isPresent()) ?
                ResponseEntity.ok(optionalUser.get().getLikedPoints().stream().map(MapPointDto::fromPoint).toList()) :
                ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
    }
}
