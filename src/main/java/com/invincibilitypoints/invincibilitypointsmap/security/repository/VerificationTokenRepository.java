package com.invincibilitypoints.invincibilitypointsmap.security.repository;

import com.invincibilitypoints.invincibilitypointsmap.security.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    @Override
    void deleteById(Long aLong);
}
