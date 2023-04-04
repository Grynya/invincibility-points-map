package com.invincibilitypoints.invincibilitypointsmap.repository;

import com.invincibilitypoints.invincibilitypointsmap.dto.ResourceDto;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query("SELECT new com.invincibilitypoints.invincibilitypointsmap.dto.ResourceDto(r.id, r.name, r.description) " +
            "FROM Resource r")
    List<ResourceDto> getAllDto();
}
