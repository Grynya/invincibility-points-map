package com.invincibilitypoints.invincibilitypointsmap.converters;

import com.invincibilitypoints.invincibilitypointsmap.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PointConverter {
    public static Point toPoint(PointDto coordinatesDto) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(coordinatesDto.lat(), coordinatesDto.lng());
        return geometryFactory.createPoint(coordinate);
    }
}
