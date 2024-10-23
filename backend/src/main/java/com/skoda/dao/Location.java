package com.skoda.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.*;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Location {
    private Address address;

    @JsonSerialize(converter = PointToPosition.class)
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @NonNull
    private Point position;

    @JsonCreator
    public Location(
            @JsonProperty("address") Address address,
            @JsonProperty("position") Position position) {
        this.address = address;
        this.position = new Point(position.getLat(), position.getLon());
    }

    private static final class PointToPosition extends StdConverter<Point, Position> {
        @Override
        public Position convert(Point point) {
            return new Position(point.getX(), point.getY());
        }
    }
}
