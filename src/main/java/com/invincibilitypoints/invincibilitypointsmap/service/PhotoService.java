package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.PointPhoto;
import com.invincibilitypoints.invincibilitypointsmap.repository.MapPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.PhotoRepository;
import com.invincibilitypoints.invincibilitypointsmap.util.LocationUriBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final MapPointRepository mapPointRepository;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));

    public PhotoService(PhotoRepository photoRepository, MapPointRepository mapPointRepository) {
        this.photoRepository = photoRepository;
        this.mapPointRepository = mapPointRepository;
    }

    @Transactional
    public ResponseEntity<?> createPhotos(MultipartFile[] photos, Long mapPointId) {
        try {
            Optional<MapPoint> mapPointOwnerOfPhotos = mapPointRepository.findById(mapPointId);
            if (mapPointOwnerOfPhotos.isEmpty())
                return ResponseEntity
                        .badRequest()
                        .body(errors.getString("invalid_user_id"));
            else {
                Set<PointPhoto> allPhotos = new HashSet<>();
                for (MultipartFile image : photos) {
                    PointPhoto pointPhoto = PointPhoto.builder()
                            .point(mapPointOwnerOfPhotos.get())
                            .fileContent(image.getBytes())
                            .fileName(image.getOriginalFilename())
                            .contentType(image.getContentType())
                            .build();
                    allPhotos.add(pointPhoto);
                    photoRepository.save(pointPhoto);
                }
                mapPointOwnerOfPhotos.get().setPhotos(allPhotos);
                mapPointRepository.save(mapPointOwnerOfPhotos.get());

                URI location = LocationUriBuilder.build(mapPointId);

                return ResponseEntity.created(location).build();
            }
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errors.getString("error_uploading_photo"));
        }
    }
}