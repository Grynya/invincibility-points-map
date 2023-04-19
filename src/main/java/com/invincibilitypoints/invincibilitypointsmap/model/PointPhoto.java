package com.invincibilitypoints.invincibilitypointsmap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_photo")
public class PointPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String contentType;

    @Column(length = 100000)
    private byte[] fileContent;

    @ManyToOne
    @JoinColumn(name="point_id", nullable=false)
    private MapPoint point;
}