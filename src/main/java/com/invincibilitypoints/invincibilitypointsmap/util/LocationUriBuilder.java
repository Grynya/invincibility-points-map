package com.invincibilitypoints.invincibilitypointsmap.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class LocationUriBuilder {
    public static URI build(Long mapPointId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(mapPointId)
                .toUri();
    }
}
