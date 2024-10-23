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

    private static final int[] numbers = {0, 11, 51, 101};

    @Override
    public void run(String... args) {
        dummyLicenseDetail();
        dummyApplications();

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
        linkedLicenseRepository.findAll().stream()
                .filter(linked -> linked.discountPercent() == 0)
                .forEach(linked -> IntStream.range(0, rand()).forEach(i -> licensesService
                        .postponeSubscriptionRenewal("Bearer " + linked.getVehicle().getToken(), linked.getLicence().getId().toHexString())));
    }

    /*
    a. Infotainment Online – one year
    b. Remote Access – one year
    c. Online data for Travel Assist, Media Streaming, Online data for Intelligent Speed Assist, Traffication
    */
    private void dummyLicenseDetail() {
        if (licenseRepository.findAll().isEmpty()) {
            List<LicenseDetail> licenseDetails = List.of(
                    LicenseDetail.builder()
                            .name(TRAFFICATION)
                            .description("The Traffication app provides drivers with real-time updates on traffic conditions to help them avoid congestion and potentially dangerous road situations. The system alerts users to road hazards, accidents, or construction, ensuring a safer and more efficient driving experience.")
                            .summary("Real-time traffic updates and road hazard alerts for a safer driving experience.")
                            .impactOfExpiredLicense("Without an active license for Traffication, you will lose access to important traffic alerts and road hazard notifications, potentially increasing travel time and exposing you to unsafe conditions.")
                            .price(generateRandomBigDecimal())
                            .subscriptionPeriod(12)
                            .licenceType(LicenceType.PAID)
                            .build(),
                    LicenseDetail.builder()
                            .name(REMOTE_ACCESS)
                            .description("Remote Access allows Skoda owners to control certain aspects of their vehicle from their smartphone. Users can remotely check vehicle status (fuel levels, battery charge), lock/unlock doors, turn on the air conditioning, and track the car’s location, providing convenience and peace of mind.")
                            .summary("Remote control of vehicle status, doors, and location from a smartphone.")
                            .impactOfExpiredLicense("If the license expires, you will no longer be able to remotely monitor your vehicle’s status or control key functions, leading to decreased convenience and potential security concerns.")
                            .price(generateRandomBigDecimal())
                            .subscriptionPeriod(12)
                            .licenceType(LicenceType.PAID)
                            .build(),
                    LicenseDetail.builder()
                            .name(INFOTAINMENT_ONLINE)
                            .description("Infotainment Online provides access to a variety of online services directly through the vehicle’s multimedia system. Drivers can get real-time information on weather, news, parking availability, traffic conditions, and access various entertainment services during trips.")
                            .summary("Real-time information and entertainment services integrated with the car’s multimedia system.")
                            .impactOfExpiredLicense("Without an active subscription, you will lose access to essential real-time information such as traffic updates and parking availability, which could result in longer travel times or difficulties finding services en route.")
                            .price(generateRandomBigDecimal())
                            .subscriptionPeriod(12)
                            .licenceType(LicenceType.PAID)
                            .build());
            licenseRepository.saveAll(licenseDetails);
        }
    }

    private void dummyApplications() {
        List<LicenseDetail> licenses = licenseRepository.findAll();
        licenses.stream()
                .filter(l -> l.getApplications().isEmpty())
                .forEach(l -> {
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