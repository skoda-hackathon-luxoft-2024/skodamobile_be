package com.skoda.controller;

import com.skoda.dto.Device;
import com.skoda.dto.POI;
import com.skoda.dto.Profile;
import com.skoda.error.ApiException;
import com.skoda.repository.DeviceRepository;
import com.skoda.repository.POIRepository;
import com.skoda.repository.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MainController {
    private final POIRepository poiRepository;
    private final ProfileRepository profileRepository;
    private final DeviceRepository deviceRepository;

    @Operation(summary = "Get all available Profiles")
    @GetMapping(value = "profiles", produces = APPLICATION_JSON_VALUE)
    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    @Operation(summary = "Update Profile for particular mobile device")
    @PostMapping(value = "profile", produces = APPLICATION_JSON_VALUE)
    public Device updateProfile(
            @Parameter(description = "Your device unique name")
            @NotBlank @RequestParam String deviceName,
            @Parameter(description = "Profile name, which you want to assign to device")
            @NotBlank @RequestParam String profileName) {
        Optional<Profile> profileOpt = profileRepository.findById(profileName);

        if (profileOpt.isEmpty()) {
            throw new ApiException(String.format("Profile '%s' not found", profileName));
        }

        Optional<Device> deviceOpt = deviceRepository.findById(deviceName);
        Device device;
        if (deviceOpt.isPresent()) {
            device = deviceOpt.get();
            device.setProfile(profileOpt.get());
            device.setName(deviceName);
        } else {
            device = new Device(deviceName, profileOpt.get(), Set.of());
            log.warn("Create new device {}", device);
        }
        return deviceRepository.save(device);
    }

    @Operation(summary = "Get charging station near location")
    @GetMapping(value = "location", produces = APPLICATION_JSON_VALUE)
    public List<GeoResult<POI>> getStationForLocation(
            @Parameter(description = "Radius of searching in KILOMETERS")
            @NotNull @RequestParam(defaultValue = "1", required = false) Double radius,
            @NotNull @RequestParam(defaultValue = "16.6746837") Double longitude,
            @NotNull @RequestParam(defaultValue = "49.2066837") Double latitude
    ) {
        Point point = new Point(latitude, longitude);
        Distance distance = new Distance(radius, Metrics.KILOMETERS);

        GeoResults<POI> result = poiRepository.findByLocationPositionNear(point, distance);
        return result.getContent();
    }

    @Operation(summary = "Add POI into favorit, for your unique device")
    @PostMapping("favorite/{id}")
    public Device addFavorite(
            @Parameter(description = "POI id", required = true)
            @NotBlank @PathVariable(name = "id") String poiId,
            @Parameter(description = "Your device unique name")
            @NotBlank @RequestParam String deviceName) {
        Optional<POI> poiOpt = poiRepository.findById(poiId);
        Optional<Profile> profileOpt = profileRepository.findFirstByName();

        if (poiOpt.isEmpty() || profileOpt.isEmpty()) {
            StringBuilder message = new StringBuilder();
            if (poiOpt.isEmpty()) {
                message.append(String.format("POI with id '%s' ", poiId));
            }
            if (profileOpt.isEmpty()) {
                message.append("and no one Profile ");
            }
            message.append("not found.");
            throw new ApiException(message.toString());
        }

        Device device = deviceRepository.findById(deviceName)
                .map(d -> d.addFavorite(poiOpt.get()))
                .orElseGet(() -> new Device(deviceName, profileOpt.get(), Set.of(poiOpt.get())));
        return deviceRepository.save(device);
    }

    @Operation(summary = "Remove POI from favorit, for your unique device")
    @DeleteMapping("favorite/{id}")
    public Device removeFavorite(
            @Parameter(description = "POI id", required = true)
            @NotBlank @PathVariable(name = "id") String poiId,
            @Parameter(description = "Your device unique name")
            @NotBlank @RequestParam String deviceName) {
        Optional<POI> poiOpt = poiRepository.findById(poiId);
        Optional<Device> deviceOpt = deviceRepository.findById(deviceName);

        if (poiOpt.isEmpty() || deviceOpt.isEmpty()) {
            StringBuilder message = new StringBuilder();
            if (poiOpt.isEmpty()) {
                message.append(String.format("POI with id '%s' ", poiId));
            }
            if (deviceOpt.isEmpty()) {
                message.append(String.format("and Device '%s' ", deviceName));
            }
            message.append("not found.");
            throw new ApiException(message.toString());
        }

        return deviceRepository.save(deviceOpt.get().removeFavorite(poiOpt.get()));
    }

    /*
    For web front only
     */
    @Operation(summary = "Get all favorites for all devicess")
    @GetMapping(value = "favorites", produces = APPLICATION_JSON_VALUE)
    public List<Device> getAllFavorites() {
        return deviceRepository.findAll();
    }
}
