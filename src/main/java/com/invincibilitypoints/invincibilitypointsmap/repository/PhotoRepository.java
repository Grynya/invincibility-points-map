package com.invincibilitypoints.invincibilitypointsmap.repository;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PointPhoto, Long> {
    void deleteByPoint(MapPoint mapPoint);
}
