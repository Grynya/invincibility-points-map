package com.invincibilitypoints.invincibilitypointsmap.security.models;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Email
    @Column(unique = true)
    @NotNull
    private String email;

    private String password;

    private Integer code;

    @OneToMany(mappedBy="userOwner")
    private Set<MapPoint> points;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EStatus userStatus = EStatus.INACTIVE;

    @ManyToMany(mappedBy = "usersWhoLiked")
    Set<MapPoint> likedPoints;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    Set<Role> roles;

}
