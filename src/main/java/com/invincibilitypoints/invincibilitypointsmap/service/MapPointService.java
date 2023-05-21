package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.converters.PointConverter;
import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.CreatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.RatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.CreateMapPointResponse;
import com.invincibilitypoints.invincibilitypointsmap.repository.MapPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.RatedPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.ResourceRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.payload.response.MessageResponse;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MapPointService {
    private final PhotoService photoService;
    private final MapPointRepository mapPointRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final RatedPointRepository ratedPointRepository;

    @Autowired
    public MapPointService(PhotoService photoService, MapPointRepository pointRepository,
                           UserRepository userRepository,
                           ResourceRepository resourceRepository,
                           RatedPointRepository ratedPointRepository) {
        this.photoService = photoService;
        this.mapPointRepository = pointRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.ratedPointRepository = ratedPointRepository;
    }

    public ResponseEntity<?> filterPointsInBounds(PointRequest pointRequest) {
        List<MapPoint> points = findByBoundsWithDislikes(pointRequest);
        return ResponseEntity.ok().body(points.stream().map(MapPointDto::fromPoint).toList());
    }

    private List<MapPoint> findByBoundsWithDislikes(PointRequest pointRequest) {
        return mapPointRepository.findByBoundsWithDislikes(
                pointRequest.getSw().lat(),
                pointRequest.getSw().lng(),
                pointRequest.getNe().lat(),
                pointRequest.getNe().lng());
    }

    @Transactional
    public ResponseEntity<?> createPoint(CreatePointRequest createPointRequest) {
        Long userId = createPointRequest.getUserId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
        }

        Point coordinates = PointConverter.toPoint(createPointRequest.getCoordinates());

        if (mapPointRepository.existsMapPointByCoordinates(coordinates)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Point with the same coordinates already exists"));
        }

        MapPoint mapPoint = buildMapPoint(createPointRequest, user.get(), coordinates);

        // Connect the resources to the map point
        Set<Resource> resources = new HashSet<>();
        for (Resource resource : createPointRequest.getResources()) {
            Optional<Resource> savedResource = resourceRepository.findById(resource.getId());
            if (savedResource.isPresent()) {
                savedResource.get().getPoints().add(mapPoint);
                resources.add(resource);
            }
        }
        mapPoint.setResources(resources);

        MapPoint mapPointCreated = mapPointRepository.save(mapPoint);

        return ResponseEntity.ok().body(new CreateMapPointResponse(mapPointCreated.getId()));
    }

    private MapPoint buildMapPoint(CreatePointRequest createPointRequest, User user, Point coordinates) {
        return MapPoint.builder()
                .name(createPointRequest.getName())
                .description(createPointRequest.getDescription())
                .phone(createPointRequest.getPhone())
                .hoursOfWork(createPointRequest.getHoursOfWork())
                .coordinates(coordinates)
                .usersWhoRated(new HashSet<>())
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

    public ResponseEntity<?> ratePoint(RatePointRequest ratePointRequest) {
        Optional<MapPoint> pointOptional = mapPointRepository.findById(ratePointRequest.getPointId());
        Optional<User> userOptional = userRepository.findById(ratePointRequest.getUserId());
        if (userOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
        else if (pointOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("Point id is invalid"));
        else {
            Optional<RatedPoint> ratedPointOptional =
                    ratedPointRepository.findByUserAndPoint(userOptional.get(), pointOptional.get());
            ratedPointOptional
                    .ifPresentOrElse(ratedPoint -> {
                                ratedPoint.setRating(ratePointRequest.getRating());
                                ratedPointRepository.save(ratedPoint);
                            },
                            () -> {
                                RatedPoint ratedPoint = RatedPoint
                                        .builder()
                                        .point(pointOptional.get())
                                        .user(userOptional.get())
                                        .rating(ratePointRequest.getRating())
                                        .build();
                                ratedPointRepository.save(ratedPoint);
                            });
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<?> getRatingOfPoint(Long pointId, Long userId) {
        Optional<MapPoint> pointOptional = mapPointRepository.findById(pointId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
        else if (pointOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("Point id is invalid"));
        else {
            Optional<RatedPoint> ratedPointOptional =
                    ratedPointRepository.findByUserAndPoint(userOptional.get(), pointOptional.get());
            return ratedPointOptional
                    .map(ratedPoint -> ResponseEntity
                            .ok()
                            .body(ratedPoint.getRating()))
                    .orElseGet(() -> ResponseEntity
                            .ok()
                            .body(ERating.NOT_RATED));
        }
    }

    public ResponseEntity<?> getPointsByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
        return ResponseEntity.ok().body(mapPointRepository.findMapPointByUserOwner(userOptional.get()).stream().map(MapPointDto::fromPoint).toList());
    }

    @Transactional
    public ResponseEntity<?> deleteMapPoint(Long pointId) {
        Optional<MapPoint> mapPointOptional = mapPointRepository.findById(pointId);
        if (mapPointOptional.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("Map point id is invalid"));
        mapPointOptional.get().getResources().forEach(resource -> {
            resource.getPoints().remove(mapPointOptional.get());
            resourceRepository.save(resource);
        });
        ratedPointRepository.deleteByPoint(mapPointOptional.get());
        photoService.deleteByMapPoint(mapPointOptional.get());
        mapPointRepository.delete(mapPointOptional.get());
        return ResponseEntity.ok().build();
    }
}