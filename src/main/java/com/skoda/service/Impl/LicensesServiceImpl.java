package com.skoda.service.Impl;

import com.skoda.converter.LicensesConverter;
import com.skoda.dao.*;
import com.skoda.dto.LicenceDetailDto;
import com.skoda.dto.LicenseRenewal;
import com.skoda.dto.LinkedLicenceDto;
import com.skoda.dto.PersonalizedData;
import com.skoda.dto.auth.DeviceType;
import com.skoda.repository.LicenseRepository;
import com.skoda.repository.LinkedLicenseRepository;
import com.skoda.service.AuthService;
import com.skoda.service.LicensesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class LicensesServiceImpl implements LicensesService {

    private final LicenseRepository licenseRepository;
    private final LinkedLicenseRepository linkedLicenseRepository;
    private final AuthService authService;

    @Override
    public List<LicenceDetailDto> getAllLicenses() {
        return LicensesConverter.INSTANCE.toLicenceDetailList(licenseRepository.findAll());
    }

    @Override
    public LicenceDetailDto getGeneralLicenceById(String licenceId) {
        LicenseDetail licenses = licenseRepository.findById(licenceId)
                .orElseThrow(() -> new NoSuchElementException("Licence not found " + licenceId));
        return LicensesConverter.INSTANCE.toDto(licenses);
    }

    @Override
    public List<LinkedLicenceDto> getLinkedLicenses(String authorizationHeader) {
        Set<LinkedLicense> linked = linkedLicenses(authorizationHeader);
        return LicensesConverter.INSTANCE.toLinkedLicenceDtoList(linked);
    }

    @Override
    public List<LinkedLicenceDto> getLinkedLicenceById(String authorizationHeader, String licenceId) {
        Set<LinkedLicense> linked = linkedLicenses(authorizationHeader);
        Map<String, List<LinkedLicense>> groupingByLicenceId = linked.stream()
                .collect(Collectors.groupingBy(e -> e.getLicence().getId().toHexString()));

        if (groupingByLicenceId.containsKey(licenceId)) {
            return LicensesConverter.INSTANCE.toLinkedLicenceDtoList(groupingByLicenceId.get(licenceId));
        }

        throw new IllegalArgumentException("Licence not found");
    }

    @Override
    public void postponeSubscriptionRenewal(String authorizationHeader, String licenceId) {
        User user = authService.getUserByToken(authorizationHeader);

        if (DeviceType.VEHICLE.equals(user.getDeviceType())) {
            Vehicle vehicle = user.asVehicle();
            LinkedLicense linked = getLinkedLicense(vehicle, licenceId);
            linked.incrementAndGet();
            linkedLicenseRepository.save(linked);
        }
        if (DeviceType.MOBILE.equals(user.getDeviceType())) {
            List<LinkedLicense> linkedLicenses = user.asMobile().getVehicles().stream()
                    .map(vehicle -> {
                        LinkedLicense linked = getLinkedLicense(vehicle, licenceId);
                        linked.incrementAndGet();
                        return linked;
                    })
                    .toList();
            linkedLicenseRepository.saveAll(linkedLicenses);
        }
    }

    @Override
    public PersonalizedData getSubscriptionRenewalAttempts(String authorizationHeader, String licenceId) {
        Vehicle vehicle = authService.getUserByToken(authorizationHeader).asVehicle();

        LinkedLicense linked = getLinkedLicense(vehicle, licenceId);

        return LicensesConverter.INSTANCE.toPersonalizedData(linked);
    }

    @Override
    public List<LinkedLicenceDto> testExpired(String authorizationHeader) {
        Set<LinkedLicense> linked = linkedLicenses(authorizationHeader);

        linked.forEach(l -> l.setPurchaseDate(Instant.now().atZone(ZoneId.systemDefault()).minusMonths(16).toInstant()));

        List<LinkedLicense> saved = linkedLicenseRepository.saveAll(linked);
        return LicensesConverter.INSTANCE.toLinkedLicenceDtoList(saved);
    }

    @Override
    public List<LinkedLicenceDto> testExpired(String authorizationHeader, String licenceId) {
        Set<LinkedLicense> linked = linkedLicenses(authorizationHeader);

        ObjectId key = new ObjectId(licenceId);

        List<LinkedLicense> founds = linked.stream()
                .filter(l -> l.getLicence().getId().equals(key))
                .peek(l -> l.setPurchaseDate(Instant.now().atZone(ZoneId.systemDefault()).minusMonths(16).toInstant()))
                .toList();
        List<LinkedLicense> saved = linkedLicenseRepository.saveAll(founds);
        return LicensesConverter.INSTANCE.toLinkedLicenceDtoList(saved);
    }

    @Override
    public List<LinkedLicenceDto> testSpecialOffer(String authorizationHeader) {
        Set<LinkedLicense> linked = linkedLicenses(authorizationHeader);

        List<LinkedLicense> founds = linked.stream()
                .filter(l -> l.getLicence().getName().equals("Infotainment Online"))
                .peek(l -> l.setSubscriptionRenewalAttempts(51))
                .toList();


        linkedLicenseRepository.saveAll(founds);
        return LicensesConverter.INSTANCE.toLinkedLicenceDtoList(founds);
    }

    @Override
    @Deprecated
    public List<LicenseRenewal> deprecatedUpdateSubscription(String authorizationHeader, String licenceId) {
        User user = authService.getUserByToken(authorizationHeader);

        List<LinkedLicense> linkeds = new ArrayList<>();

        if (DeviceType.VEHICLE.equals(user.getDeviceType())) {
            Vehicle vehicle = user.asVehicle();
            LinkedLicense linked = getLinkedLicense(vehicle, licenceId);
            linked.setSubscriptionRenewalAttempts(0);
            linked.setPurchaseDate(Instant.now());
            LinkedLicense saved = linkedLicenseRepository.save(linked);
            linkeds.add(saved);
        }
        if (DeviceType.MOBILE.equals(user.getDeviceType())) {
            List<LinkedLicense> linkedLicenses = user.asMobile().getVehicles().stream()
                    .map(vehicle -> {
                        LinkedLicense linked = getLinkedLicense(vehicle, licenceId);
                        linked.setSubscriptionRenewalAttempts(0);
                        linked.setPurchaseDate(Instant.now());
                        return linked;
                    })
                    .toList();
            List<LinkedLicense> saved = linkedLicenseRepository.saveAll(linkedLicenses);
            linkeds.addAll(saved);
        }

        log.info("[updateSubscription] vehicle: {} linked: {}", user, linkeds);

        return linkeds.stream()
                .map(l -> new LicenseRenewal(l.getId(), l.getLicence().getName(), "License successfully Renewed", true))
                .toList();
    }

    @Override
    public LicenseRenewal updateSubscriptionFromMobile(String authorizationHeader, String licenceId, String vin) {
        Mobile user = authService.getUserByToken(authorizationHeader).asMobile();
        Vehicle vehicle = user.getVehicles().stream()
                .filter(m -> m.getVin().equals(vin))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not linked to account"));

        return updateSubscription(vehicle, licenceId);
    }

    @Override
    public LicenseRenewal updateSubscriptionFromVehicle(String authorizationHeader, String licenceId) {
        Vehicle vehicle = authService.getUserByToken(authorizationHeader).asVehicle();

        return updateSubscription(vehicle, licenceId);
    }


    private Set<LinkedLicense> linkedLicenses(String authorizationHeader) {
        User user = authService.getUserByToken(authorizationHeader);

        Set<LinkedLicense> linkedLicence = new HashSet<>();
        if (DeviceType.VEHICLE.equals(user.getDeviceType())) {
            Vehicle vehicle = user.asVehicle();
            linkedLicence = vehicle.getLinkedLicenses();
        }
        if (DeviceType.MOBILE.equals(user.getDeviceType())) {
            Mobile mobile = user.asMobile();
            linkedLicence = mobile.getVehicles().stream()
                    .flatMap(e -> e.getLinkedLicenses().stream())
                    .collect(Collectors.toSet());
        }
        return linkedLicence;
    }

    private LinkedLicense getLinkedLicense(Vehicle user, String licenceId) {
        Set<LinkedLicense> linkedLicenses = user.getLinkedLicenses();

        ObjectId key = new ObjectId(licenceId);

        return linkedLicenses.stream()
                .filter(e -> e.getLicence().getId().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Licence not found"));
    }

    private LicenseRenewal updateSubscription(Vehicle user, String licenceId) {
        LinkedLicense linked = getLinkedLicense(user, licenceId);

        linked.setSubscriptionRenewalAttempts(0);
        linked.setPurchaseDate(Instant.now());
        LinkedLicense saved = linkedLicenseRepository.save(linked);

        return new LicenseRenewal(saved.getId(), saved.getLicence().getName(), "License successfully Renewed", true);
    }
}
