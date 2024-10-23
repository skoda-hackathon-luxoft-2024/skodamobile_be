package com.skoda.repository;

import com.skoda.dao.LicenseDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface LicenseRepository extends MongoRepository<LicenseDetail, String> {

    List<LicenseDetail> findAllByNameIn(Collection<String> name);
}
