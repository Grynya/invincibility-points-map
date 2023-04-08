package com.invincibilitypoints.invincibilitypointsmap.security.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.invincibilitypoints.invincibilitypointsmap.security.exception.TokenRefreshException;
import com.invincibilitypoints.invincibilitypointsmap.security.models.RefreshToken;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.RefreshTokenRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RefreshTokenService {
    @Value("${jwt_refresh_expiration_ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        if(userRepository.findById(userId).isPresent())
            refreshToken.setUser(userRepository.findById(userId).get());

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) throws Exception {
        if (userRepository.findById(userId).isPresent())
            refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
        else throw new Exception(String.format("No user with id: %d", userId));
    }
}
