package com.invincibilitypoints.invincibilitypointsmap.security.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.invincibilitypoints.invincibilitypointsmap.security.security.service.UserDetailsImpl;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final Key key;
    private final int jwtExpirationMs;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));

    public JwtUtils(@Value("${jwt_expiration_ms}")int jwtExpirationMs) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromEmail(userPrincipal.getUsername());
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("token before error:  "+authToken);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error(errors.getString("invalid_jwt_signature"), e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error(errors.getString("invalid_jwt_token"), e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error(errors.getString("jwt_token_expired"), e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error(errors.getString("unsupported_jwt_token"), e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error(errors.getString("empty_jwt_claims"), e.getMessage());
        }

        return false;
    }

    public ResponseEntity<Boolean> isLoggedIn(String authToken) {
        try {
            if (authToken != null && this.validateJwtToken(authToken)) {
                String email = this.getEmailFromJwtToken(authToken);
                return ResponseEntity.ok().body(email != null);
            }
        } catch (Exception e) {
            logger.error(errors.getString("invalid_jwt_token"), e.getMessage());
        }
        return ResponseEntity.ok().body(false);
    }
}
