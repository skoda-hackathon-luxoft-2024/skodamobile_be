package com.skoda.repository;

import com.skoda.dao.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    Optional<Vehicle> findByUsername(String vin);

    Optional<Vehicle> findByParingNumber(String paringNumber);

    boolean existsByParingNumber(String randomSixDigit);
}
