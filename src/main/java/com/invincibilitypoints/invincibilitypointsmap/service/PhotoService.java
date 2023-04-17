package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;
import com.invincibilitypoints.invincibilitypointsmap.repository.PhotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Set<PointPhoto> saveListOfPhotos(MultipartFile[] photos, MapPoint mapPoint) throws IOException {
        Set<PointPhoto> result = new HashSet<>();
        for (MultipartFile image : photos) {
            PointPhoto pointPhoto = PointPhoto.builder()
                    .point(mapPoint)
                    .fileContent(image.getBytes())
                    .fileName(image.getOriginalFilename())
                    .contentType(image.getContentType())
                    .build();
            result.add(pointPhoto);
            photoRepository.save(pointPhoto);
        }
        return result;
    }
}