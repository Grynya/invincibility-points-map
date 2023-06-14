package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.UserDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.enums.ETokenVerificationStatus;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.TokenVerificationResponse;
import com.invincibilitypoints.invincibilitypointsmap.repository.RefreshTokenRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.VerificationTokenRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.exception.TokenRefreshException;
import com.invincibilitypoints.invincibilitypointsmap.security.model.RefreshToken;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.model.VerificationToken;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.LoginRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.request.TokenRefreshRequest;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.JwtResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.TokenRefreshResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.security.jwt.JwtUtils;
import com.invincibilitypoints.invincibilitypointsmap.security.security.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AuthService {
    @Value("${jwt_refresh_expiration_ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${jwt_expiration_ms}")int jwtExpirationMs;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public AuthService(RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public ResponseEntity<?> authenticateUserWithCredentials(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getUserStatus().equals(EStatus.INACTIVE)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(errors.getString("inactive_user")));
        }

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshToken refreshToken = createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), userDetails.getId(),
                userDetails.getName(), userDetails.getSurname(), userDetails.getEmail(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())));
    }
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .build();

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

    public JwtResponse generateTokens(User user) {

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        RefreshToken refreshToken = createRefreshToken(user.getId());

        return new JwtResponse(jwt, jwtExpirationMs, refreshToken.getToken(), user.getId(),
                user.getEmail(), user.getSurname(), user.getEmail(),
                user
                        .getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    private RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString())
                .build();

        userRepository.findById(userId)
                .ifPresent(refreshToken::setUser);

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public ResponseEntity<?> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                    RefreshToken updatedRefreshToken = createRefreshToken(user.getId());
                    return ResponseEntity
                            .ok(new TokenRefreshResponse(token, updatedRefreshToken.getToken(), jwtExpirationMs));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }


    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    void deleteByUserId(Long userId) throws Exception {
        if (userRepository.findById(userId).isPresent())
            refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
        else throw new Exception(String.format(errors.getString("invalid_user_id")));
    }

    public ResponseEntity<?> logout(){
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            Long userId = userDetails.getId();
            deleteByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    public ResponseEntity<Boolean> isLoggedIn(String authToken) {
        try {
            if (authToken != null && jwtUtils.validateJwtToken(authToken)) {
                String email = jwtUtils.getEmailFromJwtToken(authToken);
                return ResponseEntity.ok().body(email != null);
            }
        } catch (Exception e) {
            logger.error(errors.getString("invalid_jwt_token"), e.getMessage());
        }
        return ResponseEntity.ok().body(false);
    }

    public ResponseEntity<?> getUserInfoByAccessToken(String accessToken) {
        String username = jwtUtils.getEmailFromJwtToken(accessToken);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse(errors.getString("invalid_user_id")));
        return ResponseEntity.ok(UserDto.fromUser(user.get()));
    }
}
