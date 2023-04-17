package com.invincibilitypoints.invincibilitypointsmap.payload.request;

import com.invincibilitypoints.invincibilitypointsmap.dto.PointDto;
import com.invincibilitypoints.invincibilitypointsmap.model.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
@ToString
public class CreatePointRequest {
    @NotNull
    private String name;
    private String description;
    @NotNull
    private String phone;
    private String hoursOfWork;
    @NotNull
    private PointDto coordinates;
    private MultipartFile[] photos;
    private Set<Resource> resources;
    private Long userId;
}