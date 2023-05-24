package com.invincibilitypoints.invincibilitypointsmap.payload.response;

import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;

public record RatingResponse(ERating eRating, Integer numOfLikes, Integer numOfDislikes){}