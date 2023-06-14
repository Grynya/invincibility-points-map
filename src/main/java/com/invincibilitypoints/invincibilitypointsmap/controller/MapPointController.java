package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.payload.request.CreatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.RatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.service.MapPointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class MapPointController {
    private final MapPointService mapPointService;

    @Autowired
    public MapPointController(MapPointService pointService) {
        this.mapPointService = pointService;
    }

//    @GetMapping("/point/{mapPointId}")
//    public ResponseEntity<?> getMapPointById(@Valid @PathVariable Long mapPointId) {
//        return mapPointService.getMapPointById(mapPointId);
//    }
    @PostMapping("/public/point/getAll")
    public ResponseEntity<?> getPoints(@Valid @RequestBody PointRequest pointRequest) {
        return mapPointService.filterPointsInBounds(pointRequest);
    }
    @GetMapping("/point/getAllByUser")
    public ResponseEntity<?> getPointsByUser(@Valid @RequestParam Long userId) {
        return mapPointService.getPointsByUser(userId);
    }
    @PostMapping("/point")
    public ResponseEntity<?> createPoint(@Valid @RequestBody CreatePointRequest createPointRequest) {
        return mapPointService.createPoint(createPointRequest);
    }
    @PostMapping("/point/rate")
    public ResponseEntity<?> likePoint(@Valid @RequestBody RatePointRequest ratePointRequest) {
        return mapPointService.ratePoint(ratePointRequest);
    }
    @GetMapping("/point/getRating")
    public ResponseEntity<?> getRatingOfPoint(@Valid @RequestParam Long mapPointId,
                                              @Valid @RequestParam Long userId) {
        return mapPointService.getRatingOfPoint(mapPointId, userId);
    }

    @DeleteMapping("/admin/point")
    public ResponseEntity<?> deleteUser(@Valid @RequestParam Long pointId) {
        return mapPointService.deleteMapPoint(pointId);
    }

}