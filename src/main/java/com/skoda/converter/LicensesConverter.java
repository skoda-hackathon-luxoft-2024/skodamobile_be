package com.skoda.converter;


import com.skoda.dao.Application;
import com.skoda.dao.LicenseDetail;
import com.skoda.dao.LinkedLicense;
import com.skoda.dto.ApplicationDto;
import com.skoda.dto.LicenceDetailDto;
import com.skoda.dto.LinkedLicenceDto;
import com.skoda.dto.PersonalizedData;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LicensesConverter {
    LicensesConverter INSTANCE = Mappers.getMapper(LicensesConverter.class);

    LicenceDetailDto toDto(LicenseDetail dao);

    List<LicenceDetailDto> toLicenceDetailList(Collection<LicenseDetail> daos);

    @Mapping(target = "id", source = "licence.id")
    @Mapping(target = "name", source = "licence.name")
    @Mapping(target = "description", source = "licence.description")
    @Mapping(target = "summary", source = "licence.summary")
    @Mapping(target = "impactOfExpiredLicense", source = "licence.impactOfExpiredLicense")
    @Mapping(target = "netPrice", source = "licence.netPrice")
    @Mapping(target = "vatPercent", source = "licence.vatPercent")
    @Mapping(target = "vin", source = "vehicle.username")
    @Mapping(target = "subscriptionPeriod", source = "licence.subscriptionPeriod")
    @Mapping(target = "licenceType", source = "licence.licenceType")
    @Mapping(target = "discountPercent", source = ".", qualifiedByName = "discountPercent")
    @Mapping(target = "vat", source = ".", qualifiedByName = "calculateVat")
    @Mapping(target = "discountNetPrice", source = ".", qualifiedByName = "discountNetPrice")
    LinkedLicenceDto toDto(LinkedLicense dao);

    @Mapping(target = "licence", source = "licence.id")
    ApplicationDto toDto(Application dao);

    @Mapping(target = "discountPercent", source = ".", qualifiedByName = "discountPercent")
    PersonalizedData toPersonalizedData(LinkedLicense linked);

    List<ApplicationDto> toApplicationDtoList(List<Application> dao);

    List<LinkedLicenceDto> toLinkedLicenceDtoList(Collection<LinkedLicense> daos);

    default String map(ObjectId value) {
        return value != null ? value.toHexString() : null;
    }

    default ObjectId map(String value) {
        return value != null ? new ObjectId(value) : null;
    }

    @Named("calculateVat")
    default BigDecimal calculateVat(LinkedLicense license) {
        BigDecimal vatRate = BigDecimal.valueOf(license.getLicence().getVatPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal vatAmount = discountNetPrice(license).multiply(vatRate);

        return vatAmount.setScale(0, RoundingMode.HALF_UP);
    }

    @Named("discountNetPrice")
    default BigDecimal discountNetPrice(LinkedLicense linked) {
        int discountPercent = discountPercent(linked);

        BigDecimal percentage = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal amountToSubtract = linked.getLicence().getNetPrice().multiply(percentage);
        BigDecimal result = linked.getLicence().getNetPrice().subtract(amountToSubtract);
        return result.setScale(0, RoundingMode.HALF_UP);
    }

    @Named("discountPercent")
    default int discountPercent(LinkedLicense linked) {
        int discountPercent = 0;
        if (linked.getSubscriptionRenewalAttempts() > 10) {
            discountPercent = 15;
        }
        if (linked.getSubscriptionRenewalAttempts() > 50) {
            discountPercent = 30;
        }
        if (linked.getSubscriptionRenewalAttempts() > 100) {
            discountPercent = 50;
        }
        return discountPercent;
    }
}