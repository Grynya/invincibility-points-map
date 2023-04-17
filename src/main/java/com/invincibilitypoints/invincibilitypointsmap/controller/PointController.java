package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.payload.request.CreatePointRequest;
import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.service.PointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class PointController {
    private final PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
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
    @PostMapping("/point")
    public ResponseEntity<?> createPoint(@Valid @RequestBody CreatePointRequest createPointRequest) {
        return pointService.createPoint(createPointRequest);
    }
}