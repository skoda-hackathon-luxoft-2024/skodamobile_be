package com.skoda.service.Impl;

import com.skoda.dao.Mobile;
import com.skoda.dao.User;
import com.skoda.dao.Vehicle;
import com.skoda.dto.auth.DeviceType;
import com.skoda.dto.pairing.PairedAccountsDto;
import com.skoda.dto.pairing.ParingNumberDto;
import com.skoda.repository.MobileRepository;
import com.skoda.repository.VehicleRepository;
import com.skoda.service.AuthService;
import com.skoda.service.ParingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ParingServiceImpl implements ParingService {

    private final VehicleRepository vehicleRepository;
    private final MobileRepository mobileRepository;
    private final AuthService authService;

    private final static SecureRandom secureRandom = new SecureRandom();

    @Override
    public ParingNumberDto getNumberForPairing(String authorizationHeader) {
        Vehicle vehicle = authService.getUserByToken(authorizationHeader).asVehicle();

        return Optional.of(vehicle.getParingNumber())
                .map(ParingNumberDto::new)
                .orElseGet(() -> {

                    String randomSixDigit;
                    do {
                        randomSixDigit = String.valueOf(100000 + secureRandom.nextLong(900000));
                    } while (vehicleRepository.existsByParingNumber(randomSixDigit));

                    vehicle.setParingNumber(randomSixDigit);
                    Vehicle saved = vehicleRepository.save(vehicle);

                    return new ParingNumberDto(saved.getParingNumber());
                });
    }

    @Override
    public void pairMobileWithVehicle(String authorizationHeader, String paringNumber) {
        Vehicle vehicle = vehicleRepository.findByParingNumber(paringNumber)
                .orElseThrow(() -> new NoSuchElementException("Invalid paring number: " + paringNumber));

        Mobile mobile = authService.getUserByToken(authorizationHeader).asMobile();
        mobile.addVehicle(vehicle);
        mobileRepository.save(mobile);

        vehicle.addMobile(mobile);
        vehicleRepository.save(vehicle);
    }

    @Override
    public PairedAccountsDto getPairedAccounts(String authorizationHeader) {
        User user = authService.getUserByToken(authorizationHeader);

        List<String> list = new ArrayList<>();
        if (DeviceType.VEHICLE.equals(user.getDeviceType())) {
            list = user.asVehicle().getMobile().stream()
                    .map(User::getUsername)
                    .toList();
        }
        if (DeviceType.MOBILE.equals(user.getDeviceType())) {
            list = user.asMobile().getVehicles().stream()
                    .map(Vehicle::getVin)
                    .toList();
        }

        return new PairedAccountsDto(list);
    }

    @Override
    public void unpair(String authorizationHeader, String accountToRemove) {
        User user = authService.getUserByToken(authorizationHeader);

        Vehicle vehicle = null;
        Mobile mobile = null;
        if (DeviceType.VEHICLE.equals(user.getDeviceType())) {
            vehicle = user.asVehicle();
            mobile = vehicle.getMobile().stream()
                    .filter(m -> m.getUsername().equals(accountToRemove))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Not found account for unpairing"));
        }
        if (DeviceType.MOBILE.equals(user.getDeviceType())) {
            mobile = user.asMobile();
            vehicle = mobile.getVehicles().stream()
                    .filter(m -> m.getUsername().equals(accountToRemove))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Not found account for unpairing"));
        }
        if (vehicle != null && mobile != null) {
            vehicle.removeMobile(mobile);
            vehicleRepository.save(vehicle);

            mobile.removeVehicle(vehicle);
            vehicleRepository.save(vehicle);
        }
    }
}
