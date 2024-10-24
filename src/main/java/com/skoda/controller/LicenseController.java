package com.skoda.controller;

import com.skoda.dto.LicenceDetailDto;
import com.skoda.dto.LicenseRenewal;
import com.skoda.dto.LinkedLicenceDto;
import com.skoda.dto.PersonalizedData;
import com.skoda.service.LicensesService;
import com.skoda.validation.ValidObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@Slf4j
public class LicenseController {
    private final LicensesService licensesService;

    @Operation(summary = "Get all available/existing licenses")
    @GetMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
    public List<LicenceDetailDto> getAllLicenses() {
        return licensesService.getAllLicenses();
    }

    @Operation(summary = "Get details generally about particular licence")
    @GetMapping(value = "/details/{licenceId}", produces = APPLICATION_JSON_VALUE)
    public LicenceDetailDto getGeneralLicenceById(
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        return licensesService.getGeneralLicenceById(licenceId);
    }

    @Operation(summary = "Get all linked licenses for particular device")
    @GetMapping(value = "/linked", produces = APPLICATION_JSON_VALUE)
    public List<LinkedLicenceDto> getLinkedLicenses(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return licensesService.getLinkedLicenses(authorizationHeader);
    }

    @Operation(summary = "Get details about linked licence")
    @GetMapping(value = "/linked/{licenceId}", produces = APPLICATION_JSON_VALUE)
    public List<LinkedLicenceDto> getLinkedLicenceById(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        return licensesService.getLinkedLicenceById(authorizationHeader, licenceId);
    }

    @Operation(summary = "Notify the back-end that Client cancel/postpone subscription Renewal")
    @PostMapping(value = "/postpone/{licenceId}", produces = APPLICATION_JSON_VALUE)
    public void postponeSubscriptionRenewal(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        licensesService.postponeSubscriptionRenewal(authorizationHeader, licenceId);
    }

    @Operation(summary = "Request Licence usage data. Get Attempts of cancel/postpone subscription Renewal")
    @GetMapping(value = "/postpone/{licenceId}", produces = APPLICATION_JSON_VALUE)
    public PersonalizedData getSubscriptionProlongation(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        return licensesService.getSubscriptionRenewalAttempts(authorizationHeader, licenceId);
    }

    @Operation(summary = "Send prolongation request. Update Subscription for licenses")
    @PutMapping(value = "/{licenceId}", produces = APPLICATION_JSON_VALUE)
    public List<LicenseRenewal> updateSubscription(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        return licensesService.updateSubscription(authorizationHeader, licenceId);
    }

    @Operation(summary = "Test purpose only! Make All subscriptions Expired")
    @PostMapping(value = "/test-expired")
    public List<LinkedLicenceDto> testExpired(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return licensesService.testExpired(authorizationHeader);
    }

    @Operation(summary = "Test purpose only! Make 'Infotainment Online' Expired")
    @PostMapping(value = "/test-expired/{licenceId}")
    public List<LinkedLicenceDto> testExpired(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Licence ID")
            @PathVariable("licenceId") @ValidObjectId String licenceId) {
        return licensesService.testExpired(authorizationHeader, licenceId);
    }

    @Operation(summary = "Test purpose only! Make discount/SpecialOffer for 'Infotainment Online'")
    @PostMapping(value = "/test-discount")
    public List<LinkedLicenceDto> testSpecialOffer(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return licensesService.testSpecialOffer(authorizationHeader);
    }
}