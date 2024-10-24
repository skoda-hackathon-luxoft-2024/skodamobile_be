package com.skoda.service;

import com.skoda.dto.LicenceDetailDto;
import com.skoda.dto.LicenseRenewal;
import com.skoda.dto.LinkedLicenceDto;
import com.skoda.dto.PersonalizedData;

import java.util.List;

public interface LicensesService {
    List<LicenceDetailDto> getAllLicenses();

    LicenceDetailDto getGeneralLicenceById(String licenceId);

    List<LinkedLicenceDto> getLinkedLicenses(String authorizationHeader);

    List<LinkedLicenceDto> getLinkedLicenceById(String authorizationHeader, String licenceId);

    List<LicenseRenewal> updateSubscription(String authorizationHeader, String licenceId);

    void postponeSubscriptionRenewal(String authorizationHeader, String licenceId);

    PersonalizedData getSubscriptionRenewalAttempts(String authorizationHeader, String licenceId);

    List<LinkedLicenceDto> testExpired(String authorizationHeader);

    List<LinkedLicenceDto> testExpired(String authorizationHeader, String licenceId);

    List<LinkedLicenceDto> testSpecialOffer(String authorizationHeader);

}
