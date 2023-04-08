package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.model.Point;
import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;

import java.util.Set;
public record PointDto(Long id, String name, String description, String hoursOfWork,
                       String phone, String address, Set<PointPhoto> photos, User userOwner,
                       Set<Resource> resources) {

    public static PointDto fromPoint(Point point) {
        return new PointDto(point.getId(), point.getName(), point.getDescription(),
                point.getHoursOfWork(), point.getPhone(), point.getAddress(),
                point.getPhotos(), point.getUserOwner(), point.getResources());
    }
}
