package com.skoda.converter;


import com.skoda.dao.Application;
import com.skoda.dao.LicenseDetail;
import com.skoda.dao.LinkedLicense;
import com.skoda.dto.ApplicationDto;
import com.skoda.dto.LicenceDetailDto;
import com.skoda.dto.LinkedLicenceDto;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LicensesConverter {
    LicensesConverter INSTANCE = Mappers.getMapper(LicensesConverter.class);

    LicenceDetailDto toDto(LicenseDetail dao);

    List<LicenceDetailDto> toLicenceDetailList(Collection<LicenseDetail> daos);

    @Mapping(target = "id", source = "licence.id")
    @Mapping(target = "name", source = "licence.name")
    @Mapping(target = "subscriptionPeriod", source = "licence.subscriptionPeriod")
    @Mapping(target = "licenceType", source = "licence.licenceType")
    @Mapping(target = "vin", source = "vehicle.username")
    @Mapping(target = "price", source = "licence.price")
    LinkedLicenceDto toDto(LinkedLicense dao);

    @Mapping(target = "licence", source = "licence.id")
    ApplicationDto toDto(Application dao);
    List<ApplicationDto> toApplicationDtoList(List<Application> dao);

    List<LinkedLicenceDto> toLinkedLicenceDtoList(Collection<LinkedLicense> daos);

    default String map(ObjectId value) {
        return value != null ? value.toHexString() : null;
    }

    default ObjectId map(String value) {
        return value != null ? new ObjectId(value) : null;
    }

}