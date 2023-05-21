package com.invincibilitypoints.invincibilitypointsmap.security.models;

import com.invincibilitypoints.invincibilitypointsmap.model.MapPoint;
import com.invincibilitypoints.invincibilitypointsmap.enums.EStatus;
import com.invincibilitypoints.invincibilitypointsmap.model.RatedPoint;
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

    @OneToMany(mappedBy = "user")
    Set<RatedPoint> ratedPoints;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles;

}
