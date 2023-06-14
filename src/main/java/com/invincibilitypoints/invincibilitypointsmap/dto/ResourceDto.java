package com.invincibilitypoints.invincibilitypointsmap.dto;

import com.invincibilitypoints.invincibilitypointsmap.model.Resource;

import java.util.Set;
import java.util.stream.Collectors;

public record ResourceDto(Long id, String name, String description) {
    public static ResourceDto fromResource(Resource resource) {
        return new ResourceDto(resource.getId(), resource.getName(), resource.getDescription());
    }
    public static Set<ResourceDto> fromResourcesSet(Set<Resource> resources) {
        return resources.stream().map(ResourceDto::fromResource).collect(Collectors.toSet());
    }
}