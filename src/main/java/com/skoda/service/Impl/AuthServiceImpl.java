package com.skoda.service.Impl;

import com.skoda.config.JwtTokenProvider;
import com.skoda.dao.*;
import com.skoda.dto.auth.AuthResponseDto;
import com.skoda.dto.auth.DeviceType;
import com.skoda.dto.auth.LoginDto;
import com.skoda.dto.auth.ValidationTokenResponseDto;
import com.skoda.repository.LicenseRepository;
import com.skoda.repository.LinkedLicenseRepository;
import com.skoda.repository.MobileRepository;
import com.skoda.repository.VehicleRepository;
import com.skoda.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final VehicleRepository vehicleRepository;
    private final MobileRepository mobileRepository;
    private final LicenseRepository licenseRepository;
    private final LinkedLicenseRepository linkedLicenseRepository;

    private final static SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public AuthResponseDto loginVehicle(String vin) {
        Authentication authentication = getAuthentication(new LoginDto(vin, vin));

        return vehicleRepository.findByUsername(vin)
                .map(user -> {
                    log.info("[loginVehicle] exist {}", user);
                    return new AuthResponseDto(user.getToken());
                })
                .orElseGet(() -> {
                    String token = jwtTokenProvider.generateToken(authentication);

                    String randomSixDigit;
                    do {
                        randomSixDigit = String.valueOf(100000 + secureRandom.nextLong(900000));
                    } while (vehicleRepository.existsByParingNumber(randomSixDigit));

                    final Vehicle user = Vehicle.builder()
                            .username(vin)
                            .token(token)
                            .paringNumber(randomSixDigit)
                            .build();
                    final Vehicle savedUser = vehicleRepository.save(user);

                    List<LicenseDetail> all = licenseRepository.findAll();
                    List<LinkedLicense> linked = all.stream()
                            .map(l -> LinkedLicense.builder()
                                    .licence(l)
                                    .vehicle(savedUser)
                                    .build())
                            .toList();
                    List<LinkedLicense> linkedLicenses = linkedLicenseRepository.saveAll(linked);

                    savedUser.extendLicenses(linkedLicenses);
                    Vehicle saved = vehicleRepository.save(savedUser);

                    log.info("[loginVehicle] created {}", saved);

                    return new AuthResponseDto(token);
                });
    }

    @Override
    public AuthResponseDto loginMobile(LoginDto loginDto) {
        Authentication authentication = getAuthentication(loginDto);

        return mobileRepository.findByUsername(loginDto.getUsername())
                .map(user -> {
                    if (user.getPassword().equals(loginDto.getPassword())) {
                        log.info("[loginMobile] exist {}", user);
                        return new AuthResponseDto(user.getToken());
                    }
                    throw new BadCredentialsException("Wrong password");
                }).orElseGet(() -> {
                    String token = jwtTokenProvider.generateToken(authentication);
                    Mobile user = Mobile.builder()
                            .username(loginDto.getUsername())
                            .password(loginDto.getPassword())
                            .token(token)
                            .build();
                    Mobile saved = mobileRepository.save(user);

                    log.info("[loginMobile] created {}", saved);

                    return new AuthResponseDto(token);
                });
    }

    @Override
    public ValidationTokenResponseDto validateToken(String token, DeviceType deviceType) {
        String username = jwtTokenProvider.getUsername(token);
        Optional<? extends User> user = switch (deviceType) {
            case VEHICLE -> vehicleRepository.findByUsername(username);
            case MOBILE -> mobileRepository.findByUsername(username);
        };
        return user
                .map(u -> new ValidationTokenResponseDto("Valid token for: " + u.getUsername(), true))
                .orElse(new ValidationTokenResponseDto("Not valid token", false));
    }

    @Override
    public User getUserByToken(String authorizationHeader) {
        String username = jwtTokenProvider.getUsername(authorizationHeader);
        User user = getUserByUserName(username);
        log.info("[getUserByToken] {}", user);
        return user;
    }

    @Override
    public User getUserByUserName(String username) {
        Optional<Vehicle> vehicle = vehicleRepository.findByUsername(username);
        if (vehicle.isPresent()) {
            return vehicle.get();
        }

        Optional<Mobile> mobile = mobileRepository.findByUsername(username);
        if (mobile.isPresent()) {
            return mobile.get();
        }

        throw new UsernameNotFoundException("User not exists");
    }

    private Authentication getAuthentication(LoginDto loginDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}
