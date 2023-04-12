package com.invincibilitypoints.invincibilitypointsmap.controller;

import com.invincibilitypoints.invincibilitypointsmap.payload.request.PointRequest;
import com.invincibilitypoints.invincibilitypointsmap.service.PointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/point")
public class PointController {
    private final PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }
    @PostMapping
    public List<?> getPoints(@Valid @RequestBody PointRequest pointRequest) {
        return pointService.filterPointsInBounds(pointRequest);
    }
}