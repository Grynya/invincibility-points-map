package com.invincibilitypoints.invincibilitypointsmap.repository;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapPointRepository extends JpaRepository<MapPoint, Long> {

    boolean existsMapPointByCoordinates(Point coordinates);

    @Query(value = "SELECT mp.*, " +
            "(SELECT COUNT(*) FROM rated_point rp WHERE rp.point_id = mp.id AND rp.rating = 'DISLIKED') AS dislike_count " +
            "FROM map_point mp " +
            "WHERE ST_Intersects(coordinates, ST_MakeEnvelope(Point(:swLat, :swLng), Point(:neLat, :neLng))) " +
            "HAVING dislike_count < 20",
            nativeQuery = true)
    List<MapPoint> findByBoundsWithDislikes(@Param("swLat") Double swLat, @Param("swLng") Double swLng,
                                            @Param("neLat") Double neLat, @Param("neLng") Double neLng);

    List<MapPoint> findMapPointByUserOwner(User user);
}