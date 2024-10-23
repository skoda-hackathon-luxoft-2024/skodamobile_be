package com.skoda.repository;

import com.skoda.dao.Mobile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MobileRepository extends MongoRepository<Mobile, String> {
    Optional<Mobile> findByUsername(String username);
}
