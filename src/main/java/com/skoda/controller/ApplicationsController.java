package com.skoda.controller;

import com.skoda.converter.LicensesConverter;
import com.skoda.dao.Application;
import com.skoda.dto.ApplicationDto;
import com.skoda.repository.ApplicationRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationsController {

    private final ApplicationRepository applicationRepository;

    @Operation(summary = "Get all Application")
    @GetMapping(value = "/all", produces = APPLICATION_JSON_VALUE)
    public List<ApplicationDto> getAll() {
        List<Application> all = applicationRepository.findAll();
        return LicensesConverter.INSTANCE.toApplicationDtoList(all);
    }
}
