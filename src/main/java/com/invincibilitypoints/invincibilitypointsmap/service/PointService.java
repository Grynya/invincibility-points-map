package com.invincibilitypoints.invincibilitypointsmap.service;

import com.invincibilitypoints.invincibilitypointsmap.dto.MapPointDto;
import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.repository.MapPointRepository;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    private final MapPointRepository mapPointRepository;
    private final UserRepository userRepository;

    @Autowired
    public PointService(MapPointRepository pointRepository, UserRepository userRepository) {
        this.mapPointRepository = pointRepository;
        this.userRepository = userRepository;
    }

    public List<MapPointDto> filterPointsInBounds(PointRequest pointRequest) {
        List<MapPoint> points =  mapPointRepository
                        .findByBounds(
                                pointRequest.getSw().lat(),
                                pointRequest.getSw().lng(),
                                pointRequest.getNe().lat(),
                                pointRequest.getNe().lng());
//        System.out.println(points);
        return points.stream().map(MapPointDto::fromPoint).toList();
//        Long userId = pointRequest.getUserId();
//        if (userId != null) {
//            Optional<User> user = userRepository.findById(pointRequest.getUserId());
//            if (user.isPresent()) {
//                return ResponseEntity.ok()
//                        .body(mapPointRepository
//                                .findByBounds(
//                                        pointRequest.getSw().lat(),
//                                        pointRequest.getSw().lng(),
//                                        pointRequest.getNe().lat(),
//                                        pointRequest.getNe().lng()));
//
//            } else return ResponseEntity.badRequest().body(new MessageResponse("User id is invalid"));
//        } else return ResponseEntity.ok()
//                .body(mapPointRepository
//                        .findByBounds(
//                                pointRequest.getSw().lat(),
//                                pointRequest.getSw().lng(),
//                                pointRequest.getNe().lat(),
//                                pointRequest.getNe().lng()));
    }
}
