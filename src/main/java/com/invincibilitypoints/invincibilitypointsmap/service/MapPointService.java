package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.converters.PointConverter;
import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.CreatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.RatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.CreateMapPointResponse;
import com.invincibilitypoints.invincibilitypointsmap.payload.response.RatingResponse;
import com.invincibilitypoints.invincibilitypointsmap.repository.MapPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.RatedPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.repository.ResourceRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MapPointService {
    private final MapPointRepository mapPointRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final RatedPointRepository ratedPointRepository;
    ResourceBundle errors = ResourceBundle.getBundle("errors", new Locale("ua"));

    @Autowired
    public MapPointService(MapPointRepository pointRepository,
                           UserRepository userRepository,
                           ResourceRepository resourceRepository,
                           RatedPointRepository ratedPointRepository) {
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
            return ResponseEntity.badRequest().body(errors.getString("invalid_user_id"));
        }

        Point coordinates = PointConverter.toPoint(createPointRequest.getCoordinates());

        if (mapPointRepository.existsMapPointByCoordinatesAndIsDeleted(coordinates, false)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(errors.getString("point_already_exist"));
        }

        MapPoint mapPoint = buildMapPoint(createPointRequest, user.get(), coordinates);

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
                .isDeleted(false)
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
            return ResponseEntity.badRequest().body(errors.getString("invalid_user_id"));
        else if (pointOptional.isEmpty())
            return ResponseEntity.badRequest().body(errors.getString("invalid_point_id"));
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
            return ResponseEntity.badRequest().body(errors.getString("invalid_user_id"));
        else if (pointOptional.isEmpty())
            return ResponseEntity.badRequest().body(errors.getString("invalid_point_id"));
        else {
            Optional<RatedPoint> ratedPointOptional =
                    ratedPointRepository.findByUserAndPoint(userOptional.get(), pointOptional.get());
            Integer numOfLikes = ratedPointRepository.countAllByPointAndRating(pointOptional.get(), ERating.LIKED);
            Integer numOfDislikes = ratedPointRepository.countAllByPointAndRating(pointOptional.get(), ERating.DISLIKED);

            return ratedPointOptional
                    .map(ratedPoint -> ResponseEntity
                            .ok()
                            .body(new RatingResponse(ratedPointOptional.get().getRating(), numOfLikes, numOfDislikes)))
                    .orElseGet(() -> ResponseEntity
                            .ok()
                            .body(new RatingResponse(ERating.NOT_RATED, numOfLikes, numOfDislikes)));
        }
    }

    public ResponseEntity<?> getPointsByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return ResponseEntity.badRequest().body(errors.getString("invalid_user_id"));
        return ResponseEntity.ok().body(mapPointRepository.findMapPointByUserOwnerAndIsDeleted(userOptional.get(), false).stream().map(MapPointDto::fromPoint).toList());
    }

    public ResponseEntity<?> deleteMapPoint(Long pointId) {
        Optional<MapPoint> mapPointOptional = mapPointRepository.findById(pointId);
        if (mapPointOptional.isEmpty()) return ResponseEntity.badRequest().body(errors.getString("invalid_point_id"));
        mapPointOptional.get().setIsDeleted(true);
        mapPointRepository.save(mapPointOptional.get());
        return ResponseEntity.ok().build();
    }
}