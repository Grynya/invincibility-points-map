package com.invincibilitypoints.invincibilitypointsmap.model;

import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.Set;

@Entity @Table
@Getter @Setter @ToString
@Builder(toBuilder = true)
@NoArgsConstructor @AllArgsConstructor
public class MapPoint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    private String description;
    private String hoursOfWork;
    private String phone;
    @Column(columnDefinition = "POINT")
    private Point coordinates;
    @OneToMany(mappedBy = "point")
    private Set<PointPhoto> photos;
    @ManyToOne @JoinColumn(name = "user_owner", nullable = false)
    private User userOwner;
    @ManyToMany(mappedBy = "points")
    Set<Resource> resources;
    @OneToMany(mappedBy = "point")
    private Set<RatedPoint> usersWhoRated;
    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted;
}
