package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;

import java.util.Set;

public record MapPointDto(Long id, String name, String description, String hoursOfWork,
                          String phone, PointDto coordinates, Set<PointPhotoDto> photos, Long userId,
                          Set<ResourceDto> resources) {

    public static MapPointDto fromPoint(MapPoint mapPoint) {
        return new MapPointDto(mapPoint.getId(),
                mapPoint.getName(),
                mapPoint.getDescription(),
                mapPoint.getHoursOfWork(),
                mapPoint.getPhone(),
                new PointDto(mapPoint.getCoordinates().getX(),
                        mapPoint.getCoordinates().getY()),
                PointPhotoDto.fromPointPhotoSet(mapPoint.getPhotos()),
                mapPoint.getUserOwner().getId(),
                ResourceDto.fromResourcesSet(mapPoint.getResources()));
    }
}