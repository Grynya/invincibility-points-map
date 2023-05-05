package com.invincibilitypoints.invincibilitypointsmap.security.repository;

import com.invincibilitypoints.invincibilitypointsmap.enums.ERole;
import com.invincibilitypoints.invincibilitypointsmap.security.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
