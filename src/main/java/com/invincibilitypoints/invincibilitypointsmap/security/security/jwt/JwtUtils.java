package com.invincibilitypoints.invincibilitypointsmap.security.security.jwt;

import com.invincibilitypoints.invincibilitypointsmap.security.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final Key key;
    private final int jwtExpirationMs;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));

    public JwtUtils(@Value("${jwt_expiration_ms}")int jwtExpirationMs,
                    @Value("${jwt.secret}") String secretKey) {
        this.jwtExpirationMs = jwtExpirationMs;
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
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
}
