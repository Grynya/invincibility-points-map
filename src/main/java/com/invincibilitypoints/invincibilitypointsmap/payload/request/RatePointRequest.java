package com.invincibilitypoints.invincibilitypointsmap.payload.request;

import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RatePointRequest {
    @NotNull
    private Long pointId;
    @NotNull
    private Long userId;
    @NotNull
    private ERating rating;
}