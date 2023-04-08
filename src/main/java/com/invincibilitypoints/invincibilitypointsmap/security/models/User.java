package com.invincibilitypoints.invincibilitypointsmap.security.models;

import com.invincibilitypoints.invincibilitypointsmap.model.Point;
import com.invincibilitypoints.invincibilitypointsmap.model.UserStatus;
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

    @OneToMany(mappedBy="userOwner")
    private Set<Point> points;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.INACTIVE;

    @ManyToMany(mappedBy = "usersWhoLiked")
    Set<Point> likedPoints;
    @ManyToMany(fetch = FetchType.EAGER  , cascade = CascadeType.PERSIST)
    Set<Role> roles;
}
