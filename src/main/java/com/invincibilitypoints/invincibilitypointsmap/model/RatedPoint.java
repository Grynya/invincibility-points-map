package com.invincibilitypoints.invincibilitypointsmap.model;

import com.invincibilitypoints.invincibilitypointsmap.enums.ERating;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users_liked_points")
public class RatedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="point_id", nullable=false)
    private MapPoint point;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ERating rating;
}

