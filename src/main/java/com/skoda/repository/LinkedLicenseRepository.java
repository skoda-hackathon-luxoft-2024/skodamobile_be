package com.skoda.repository;

import com.skoda.dao.LinkedLicense;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkedLicenseRepository extends MongoRepository<LinkedLicense, String> {
}
