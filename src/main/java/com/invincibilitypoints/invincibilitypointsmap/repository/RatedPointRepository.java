package com.invincibilitypoints.invincibilitypointsmap.repository;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatedPointRepository extends JpaRepository<RatedPoint, Long> {
    Optional<RatedPoint> findByUserAndPoint(User user, MapPoint point);

    void deleteByPoint(MapPoint point);

}