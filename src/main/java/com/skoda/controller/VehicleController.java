package com.skoda.controller;

import com.skoda.dto.auth.AuthResponseDto;
import com.skoda.dto.auth.DeviceType;
import com.skoda.dto.auth.ValidationTokenResponseDto;
import com.skoda.dto.pairing.PairedAccountsDto;
import com.skoda.dto.pairing.ParingNumberDto;
import com.skoda.service.AuthService;
import com.skoda.service.ParingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/ivi")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final AuthService authService;
    private final ParingService paringService;

    @Operation(summary = "Login to obtain an authentication token")
    @PostMapping(value = "/login/{vin}", produces = APPLICATION_JSON_VALUE)
    public AuthResponseDto login(
            @Parameter(description = "Identification of IVI device - VIN code")
            @PathVariable(name = "vin") @NotBlank String deviceId) {
        return authService.loginVehicle(deviceId);
    }

    @Operation(summary = "Get number for pairing with Mobile")
    @GetMapping(value = "/number", produces = APPLICATION_JSON_VALUE)
    public ParingNumberDto getNumberForPairing(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return paringService.getNumberForPairing(authorizationHeader);
    }

    @Operation(summary = "Get list of logins of paired Mobile devices")
    @GetMapping(value = "/paired", produces = APPLICATION_JSON_VALUE)
    public PairedAccountsDto getPairedDevices(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader) {
        return paringService.getPairedAccounts(authorizationHeader);
    }

    @Operation(summary = "Unpair particular Mobile device")
    @PostMapping(value = "/unpair/{mobileAccount}", produces = APPLICATION_JSON_VALUE)
    public void unpair(
            @Parameter(description = "Token obtained during login")
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "User name of mobile device to be Unpaired")
            @PathVariable("mobileAccount") @NotBlank String accountToUnpair) {
        paringService.unpair(authorizationHeader, accountToUnpair);
    }

    @Operation(summary = "Validate token obtained during login")
    @GetMapping(value = "/validate-token/{token}", produces = APPLICATION_JSON_VALUE)
    public ValidationTokenResponseDto validateToken(
            @Parameter(description = "Token obtained during login")
            @PathVariable("token") @NotBlank String token) {
        return authService.validateToken(token, DeviceType.VEHICLE);
    }
}
