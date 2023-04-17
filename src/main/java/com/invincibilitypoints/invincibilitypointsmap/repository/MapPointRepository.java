package com.invincibilitypoints.invincibilitypointsmap.repository;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapPointRepository extends JpaRepository<MapPoint, Long> {

    @Query("SELECT photo.point FROM PointPhoto photo " +
            "WHERE photo.fileName = ?1")
    List<MapPoint> getPointByPhotoName(String photoName);

    boolean existsMapPointByCoordinates(Point coordinates);


    @Query(value = "SELECT * FROM map_point WHERE " +
            "ST_Intersects(coordinates, " +
            "ST_MakeEnvelope(Point(:swLat, :swLng), Point(:neLat, :neLng)))",
            nativeQuery = true)
    List<MapPoint> findByBounds(@Param("swLat") Double swLat, @Param("swLng") Double swLng,
                                @Param("neLat") Double neLat, @Param("neLng") Double neLng);

    @Query(value = "SELECT * FROM map_point WHERE " +
            "user_owner = :userId AND " +
            "ST_Intersects(coordinates, " +
            "ST_MakeEnvelope(Point(:swLat, :swLng), Point(:neLat, :neLng)))",
            nativeQuery = true)
    List<MapPoint> findByBoundsAndUserId(@Param("userId") Long userId,
                                         @Param("swLat") Double swLat,
                                         @Param("swLng") Double swLng,
                                         @Param("neLat") Double neLat,
                                         @Param("neLng") Double neLng);

}