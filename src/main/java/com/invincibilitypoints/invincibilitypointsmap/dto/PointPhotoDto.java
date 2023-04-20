package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;

import java.util.Set;
import java.util.stream.Collectors;

public record PointPhotoDto(Long id, String fileName, String contentType, byte[] fileContent) {
    public static PointPhotoDto fromPointPhoto(PointPhoto pointPhoto) {
        return new PointPhotoDto(
                pointPhoto.getId(),
                pointPhoto.getFileName(),
                pointPhoto.getContentType(),
                pointPhoto.getFileContent());
    }
    public static Set<PointPhotoDto> fromPointPhotoSet(Set<PointPhoto> pointPhotos) {
        return pointPhotos.stream().map(PointPhotoDto::fromPointPhoto).collect(Collectors.toSet());
    }
}
