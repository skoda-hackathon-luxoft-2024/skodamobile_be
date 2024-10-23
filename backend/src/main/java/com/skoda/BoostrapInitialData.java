package com.skoda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skoda.dao.Root;
import com.skoda.dto.Device;
import com.skoda.dto.Profile;
import com.skoda.repository.DeviceRepository;
import com.skoda.repository.POIRepository;
import com.skoda.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.skoda.dao.Categories.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoostrapInitialData implements CommandLineRunner {

    private final POIRepository poiRepository;
    private final ProfileRepository profileRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper mapper;
    private final ApplicationContext context;

    @Override
    public void run(String... args) throws IOException {
        Path path = Paths.get("").toAbsolutePath();
        Path poi = path.resolve("poi.json");
        if (!Files.exists(poi)) {
            log.warn("Not found {}", poi);
            poi = path.resolve("backend").resolve("poi.json");
            if (!Files.exists(poi)) {
                log.error("Not found {}", poi);
                SpringApplication.exit(context, () -> 0);
            }
        }

        Root root = mapper.readValue(poi.toFile(), Root.class);
        poiRepository.saveAll(root.getPois());
        log.warn("All POI written to DB, {}", poiRepository.count());

        Profile profile1 = profileRepository.save(new Profile("Business", List.of(atm, bank, cafe, primary_school, restaurant,
                secondary_school, university)));
        Profile profile2 = profileRepository.save(new Profile("Public", List.of(bakery, bar, book_store, clothing_store, convenience_store,
                department_store, drugstore, electronics_store, hospital, jewelry_store, movie_theater, night_club, park,
                pharmacy, shoe_store, shopping_mall, stadium, supermarket, tourist_attraction)));
        log.warn("Profiles written to DB, {}", profileRepository.count());
        //TODO tmp
        deviceRepository.saveAll(List.of(
                new Device("android", profile1, Set.of(root.getPois().get(1), root.getPois().get(2))),
                new Device("ios", profile2, Set.of(root.getPois().get(3), root.getPois().get(4)))));
        log.warn("Test Favorites written to DB, {}", deviceRepository.count());
    }
}
