package com.skoda;

import com.skoda.dao.Application;
import com.skoda.dao.LicenseDetail;
import com.skoda.dao.LinkedLicense;
import com.skoda.dao.Vehicle;
import com.skoda.dto.LicenceType;
import com.skoda.dto.auth.AuthResponseDto;
import com.skoda.dto.auth.LoginDto;
import com.skoda.dto.pairing.ParingNumberDto;
import com.skoda.repository.ApplicationRepository;
import com.skoda.repository.LicenseRepository;
import com.skoda.repository.LinkedLicenseRepository;
import com.skoda.repository.VehicleRepository;
import com.skoda.service.AuthService;
import com.skoda.service.LicensesService;
import com.skoda.service.ParingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class BoostrapInitialData implements CommandLineRunner {

    private final LicenseRepository licenseRepository;
    private final LinkedLicenseRepository linkedLicenseRepository;
    private final VehicleRepository vehicleRepository;
    private final ParingService paringService;
    private final AuthService authService;
    private final ApplicationRepository applicationRepository;
    private final LicensesService licensesService;

    private static final String INFOTAINMENT_ONLINE = "Infotainment Online";
    private static final String REMOTE_ACCESS = "Remote Access";
    private static final String TRAFFICATION = "Traffication";

    private static final int[] numbers = {11, 51, 101};

    @Override
    public void run(String... args) {
        List<LicenseDetail> licenses = dummyLicenseDetail();
        dummyApplications(licenses);

        // one Vehicles linked to two mobiles
        AuthResponseDto vehicleToken1 = authService.loginVehicle("VIN1");
        ParingNumberDto pairingNumber1 = paringService.getNumberForPairing("Bearer " + vehicleToken1.getAccessToken());

        AuthResponseDto mobileToken1 = authService.loginMobile(new LoginDto("user1", "password1"));
        paringService.pairMobileWithVehicle("Bearer " + mobileToken1.getAccessToken(), pairingNumber1.getNumber());
        AuthResponseDto mobileToken2 = authService.loginMobile(new LoginDto("user2", "password2"));
        paringService.pairMobileWithVehicle("Bearer " + mobileToken2.getAccessToken(), pairingNumber1.getNumber());

        // two Vehicles linked to one mobile
        AuthResponseDto vehicleToken2 = authService.loginVehicle("VIN2");
        ParingNumberDto pairingNumber2 = paringService.getNumberForPairing("Bearer " + vehicleToken2.getAccessToken());
        AuthResponseDto vehicleToken3 = authService.loginVehicle("VIN3");
        ParingNumberDto pairingNumber3 = paringService.getNumberForPairing("Bearer " + vehicleToken3.getAccessToken());

        AuthResponseDto mobileToken3 = authService.loginMobile(new LoginDto("user3", "password3"));
        paringService.pairMobileWithVehicle("Bearer " + mobileToken3.getAccessToken(), pairingNumber2.getNumber());
        paringService.pairMobileWithVehicle("Bearer " + mobileToken3.getAccessToken(), pairingNumber3.getNumber());

        dummyLinkedLicense();
        dummySpecialOffer();

        log.warn("Dummy linked: {}", linkedLicenseRepository.findAll());
    }


    private void dummySpecialOffer() {
        linkedLicenseRepository.findAll().forEach(linked -> {
            if (linked.discountPercent() == 0) {
                int range = rand();
                log.info("range {}", range);
                IntStream.range(0, range).forEach(i -> licensesService
                        .postponeSubscriptionRenewal("Bearer " + linked.getVehicle().getToken(), linked.getLicence().getId().toHexString()));
            }
        });
    }

    /*
    a. Infotainment Online – one year
    b. Remote Access – one year
    c. Online data for Travel Assist, Media Streaming, Online data for Intelligent Speed Assist, Traffication
    */
    private List<LicenseDetail> dummyLicenseDetail() {
        List<String> names = List.of(INFOTAINMENT_ONLINE, REMOTE_ACCESS, TRAFFICATION);

        List<LicenseDetail> licenseDetails = new ArrayList<>(licenseRepository.findAllByNameIn(names));

        if (licenseDetails.isEmpty()) {
            names.forEach(name -> licenseDetails.add(LicenseDetail.builder()
                    .name(name)
                    .price(generateRandomBigDecimal())
                    .subscriptionPeriod(12)
                    .licenceType(LicenceType.PAID)
                    .description(RandomStringUtils.randomAlphanumeric(15))
                    .field1(RandomStringUtils.randomAlphanumeric(15))
                    .field2(RandomStringUtils.randomAlphanumeric(15))
                    .field3(RandomStringUtils.randomAlphanumeric(15))
                    .build()));

            licenseRepository.saveAll(licenseDetails);
        }

        return licenseDetails;
    }

    private void dummyApplications(List<LicenseDetail> licenses) {
        licenses.forEach(l -> {
            if (l.getApplications().isEmpty()) {
                List<Application> applications = IntStream.range(0, 5)
                        .mapToObj(i -> Application.builder()
                                .licence(l)
                                .name(RandomStringUtils.randomAlphanumeric(10))
                                .description(RandomStringUtils.randomAlphanumeric(30))
                                .field1(RandomStringUtils.randomAlphanumeric(10))
                                .field2(RandomStringUtils.randomAlphanumeric(10))
                                .field3(RandomStringUtils.randomAlphanumeric(10))
                                .build())
                        .toList();
                List<Application> saved = applicationRepository.saveAll(applications);
                l.getApplications().addAll(saved);
            }
        });
        licenseRepository.saveAll(licenses);
    }

    private void dummyLinkedLicense() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        vehicles.forEach(vehicle -> {
            Map<String, LinkedLicense> licensesByName = vehicle.getLinkedLicenses().stream()
                    .collect(toMap(e -> e.getLicence().getName(), Function.identity()));
            licensesByName.get(INFOTAINMENT_ONLINE)
                    .setPurchaseDate(Instant.now().atZone(ZoneId.systemDefault()).minusMonths(11).minusDays(15).toInstant());
            licensesByName.get(REMOTE_ACCESS)
                    .setPurchaseDate(Instant.now().atZone(ZoneId.systemDefault()).minusMonths(16).toInstant());
            licensesByName.get(TRAFFICATION)
                    .setPurchaseDate(Instant.now().atZone(ZoneId.systemDefault()).minusMonths(6).toInstant());

            linkedLicenseRepository.saveAll(licensesByName.values());
        });
    }

    private int rand() {
        int randomIndex = ThreadLocalRandom.current().nextInt(numbers.length);
        return numbers[randomIndex];
    }

    public static BigDecimal generateRandomBigDecimal() {
        int min = 700;
        int max = 1300;

        int randomInt = ThreadLocalRandom.current().nextInt(min, max + 1);

        return BigDecimal.valueOf(randomInt);
    }

}