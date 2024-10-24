package com.skoda.controller;

import com.skoda.dto.HealthCheck;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class GeneralController {

    @Operation(summary = "Health check")
    @GetMapping(value = "/health-check", produces = APPLICATION_JSON_VALUE)
    public HealthCheck getAllLicenses() {
        return new HealthCheck(true);
    }
}
