package com.skoda.controller;


import com.skoda.dto.auth.AuthResponseDto;
import com.skoda.dto.auth.DeviceType;
import com.skoda.dto.auth.LoginDto;
import com.skoda.dto.auth.ValidationTokenResponseDto;
import com.skoda.dto.pairing.PairedAccountsDto;
import com.skoda.service.AuthService;
import com.skoda.service.ParingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
@Slf4j
public class MobileController {

    private final AuthService authService;
    private final ParingService paringService;

    @Operation(summary = "Login to obtain an authentication token")
    @PostMapping("/login")
    public AuthResponseDto login(
            @Parameter(description = "Credentials for login")
            @RequestBody @Valid LoginDto loginDto) {
        return authService.loginMobile(loginDto);
    }

    @Operation(summary = "Pairing with existing IVI Device")
    @PostMapping(value = "/pair/{number}")
    public void pairWithDevice(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "Paring Number from IVI")
            @PathVariable("number") @NotBlank String paringNumber) {
        paringService.pairMobileWithVehicle(authorizationHeader, paringNumber);
    }

    @Operation(summary = "Get ID of paired IVI device")
    @GetMapping(value = "/paired", produces = APPLICATION_JSON_VALUE)
    public PairedAccountsDto getPairedVehicleAccounts(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return paringService.getPairedAccounts(authorizationHeader);
    }

    @Operation(summary = "Unpair particular Vehicle")
    @PostMapping(value = "/unpair/{vin}", produces = APPLICATION_JSON_VALUE)
    public void unpair(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "User name of mobile device to be Unpaired")
            @PathVariable("vin") @NotBlank String vin) {
        paringService.unpair(authorizationHeader, vin);
    }

    @Operation(summary = "Validate token obtained during login")
    @GetMapping(value = "/validate-token/{token}", produces = APPLICATION_JSON_VALUE)
    public ValidationTokenResponseDto validateToken(
            @Parameter(description = "Token obtained during login")
            @PathVariable("token") @NotBlank String token) {
        return authService.validateToken(token, DeviceType.MOBILE);
    }
}

