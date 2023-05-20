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
    private final MapPointService pointService;

    @Autowired
    public MapPointController(MapPointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/point/{mapPointId}")
    public ResponseEntity<?> getMapPointById(@Valid @PathVariable Long mapPointId) {
        return pointService.getMapPointById(mapPointId);
    }
    @PostMapping("/public/point/getAll")
    public ResponseEntity<?> getPoints(@Valid @RequestBody PointRequest pointRequest) {
        return pointService.filterPointsInBounds(pointRequest);
    }
    @GetMapping("/point/getAllByUser")
    public ResponseEntity<?> getPointsByUser(@Valid @RequestParam Long userId) {
        return pointService.getPointsByUser(userId);
    }
    @PostMapping("/point")
    public ResponseEntity<?> createPoint(@Valid @RequestBody CreatePointRequest createPointRequest) {
        return pointService.createPoint(createPointRequest);
    }
    @PostMapping("/point/rate")
    public ResponseEntity<?> likePoint(@Valid @RequestBody RatePointRequest ratePointRequest) {
        return pointService.ratePoint(ratePointRequest);
    }
    @GetMapping("/point/getRating")
    public ResponseEntity<?> getRatingOfPoint(@Valid @RequestParam Long pointId, @Valid @RequestParam Long userId) {
        return pointService.getRatingOfPoint(pointId, userId);
    }
}