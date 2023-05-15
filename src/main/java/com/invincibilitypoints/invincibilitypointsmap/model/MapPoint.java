package com.invincibilitypoints.invincibilitypointsmap.model;

import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table
@ToString
public class MapPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    private String hoursOfWork;

    private String phone;

    @Column(columnDefinition = "POINT", unique = true)
    private Point coordinates;

    @OneToMany(mappedBy = "point")
    private Set<PointPhoto> photos;

    @ManyToOne
    @JoinColumn(name = "user_owner", nullable = false)
    private User userOwner;

    @ManyToMany(mappedBy = "points")
    Set<Resource> resources;

    //    @ManyToMany
//    @JoinTable(
//            name = "users_liked_points",
//            joinColumns = @JoinColumn(name = "point_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    Set<User> usersWhoLiked;
    @OneToMany(mappedBy = "point")
    private Set<RatedPoint> usersWhoRated;

}