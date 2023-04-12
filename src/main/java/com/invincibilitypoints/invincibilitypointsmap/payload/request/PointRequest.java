package com.invincibilitypoints.invincibilitypointsmap.payload.request;

import com.invincibilitypoints.invincibilitypointsmap.dto.CoordinateDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PointRequest {
    @NotNull
    private CoordinateDto sw;
    @NotNull
    private CoordinateDto ne;
    @NotNull
    private Double zoom;
    private Long userId;
}