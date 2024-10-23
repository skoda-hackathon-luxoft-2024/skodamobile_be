package com.skoda.repository;

import com.skoda.dto.POI;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface POIRepository extends MongoRepository<POI, String> {

    GeoResults<POI> findByLocationPositionNear(Point location, Distance distance);

}
