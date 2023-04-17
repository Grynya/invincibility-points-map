package com.invincibilitypoints.invincibilitypointsmap.payload.request;

import com.invincibilitypoints.invincibilitypointsmap.dto.PointDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PointRequest {
    @NotNull
    private PointDto sw;
    @NotNull
    private PointDto ne;
    @NotNull
    private Double zoom;
    private Long userId;
}