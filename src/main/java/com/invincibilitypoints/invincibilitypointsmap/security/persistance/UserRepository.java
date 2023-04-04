package com.invincibilitypoints.invincibilitypointsmap.security.persistance;

import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
