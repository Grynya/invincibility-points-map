package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;

import java.util.Set;

public record MapPointDto(Long id, String name, String description, String hoursOfWork,
                          String phone, PointDto coordinates, Set<PointPhoto> photos, Long userId,
                          Set<Resource> resources) {

    public static MapPointDto fromPoint(MapPoint point) {
        return new MapPointDto(point.getId(), point.getName(), point.getDescription(),
                point.getHoursOfWork(), point.getPhone(), new PointDto(point.getCoordinates().getX(),
                point.getCoordinates().getY()),
                point.getPhotos(), point.getUserOwner().getId(), point.getResources());
    }
}