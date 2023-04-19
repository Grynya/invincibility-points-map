package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.converters.PointConverter;
import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.exceptions.BadRequestException;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.CreatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.repository.MapPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.ResourceRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import com.invincibilitypoints.invincibilitypointsmap.util.LocationUriBuilder;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class PointService {
    private final MapPointRepository mapPointRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public PointService(MapPointRepository pointRepository,
                        UserRepository userRepository,
                        ResourceRepository resourceRepository) {
        this.mapPointRepository = pointRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

public ResponseEntity<?> filterPointsInBounds(PointRequest pointRequest) {
    List<MapPoint> points = getPointsInBounds(pointRequest);
    return ResponseEntity.ok().body(points.stream().map(MapPointDto::fromPoint).toList());
}

    private List<MapPoint> getPointsInBounds(PointRequest pointRequest) {
        Long userId = pointRequest.getUserId();
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                return mapPointRepository.findByBoundsAndUserId(
                        user.get().getId(),
                        pointRequest.getSw().lat(),
                        pointRequest.getSw().lng(),
                        pointRequest.getNe().lat(),
                        pointRequest.getNe().lng());
            } else throw new BadRequestException("User id is invalid");
        } else {
            return mapPointRepository.findByBounds(
                    pointRequest.getSw().lat(),
                    pointRequest.getSw().lng(),
                    pointRequest.getNe().lat(),
                    pointRequest.getNe().lng());
        }
    }

    @Transactional
    public ResponseEntity<?> createPoint(CreatePointRequest createPointRequest) {
            Long userId = createPointRequest.getUserId();
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
            }

            createPointRequest.getResources().removeIf(resource -> !resourceRepository.existsById(resource.getId()));

            Point coordinates = PointConverter.toPoint(createPointRequest.getCoordinates());

            if (mapPointRepository.existsMapPointByCoordinates(coordinates)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new MessageResponse("Point with the same coordinates already exists"));
            }

            MapPoint mapPoint = buildMapPoint(createPointRequest, user.get(), coordinates);

            MapPoint mapPointCreated = mapPointRepository.save(mapPoint);
            URI location = LocationUriBuilder.build(mapPointCreated.getId());

            return ResponseEntity.created(location).build();
    }

    private MapPoint buildMapPoint(CreatePointRequest createPointRequest, User user, Point coordinates) {
        return MapPoint.builder()
                .name(createPointRequest.getName())
                .description(createPointRequest.getDescription())
                .phone(createPointRequest.getPhone())
                .hoursOfWork(createPointRequest.getHoursOfWork())
                .coordinates(coordinates)
                .usersWhoLiked(new HashSet<>())
                .resources(createPointRequest.getResources())
                .userOwner(user)
                .build();
    }

    public ResponseEntity<?> getMapPointById(Long mapPointId) {
        Optional<MapPoint> mapPointOptional = mapPointRepository.findById(mapPointId);
        if (mapPointOptional.isPresent()) {
            MapPoint mapPoint = mapPointOptional.get();
            return ResponseEntity.ok(MapPointDto.fromPoint(mapPoint));
        } else return ResponseEntity.notFound().build();
    }

}