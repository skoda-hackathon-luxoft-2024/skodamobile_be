package com.skoda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skoda.dao.*;
import com.skoda.dto.POI;
import com.skoda.dto.Profile;
import com.skoda.repository.DeviceRepository;
import com.skoda.repository.POIRepository;
import com.skoda.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.skoda.dao.Categories.cafe;
import static com.skoda.dao.Categories.drugstore;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceRepository deviceRepository;
    @MockBean
    private POIRepository poiRepository;
    @MockBean
    private ProfileRepository profileRepository;


//    @BeforeEach
//    void setUp() {
//    }

    @Test
    void getProfiles() throws Exception {
        List<Profile> profiles = List.of(new Profile("profile name", List.of(drugstore, cafe)));
        String serialized = objectMapper.writeValueAsString(profiles);

        when(profileRepository.findAll()).thenReturn(profiles);
        this.mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(content().string(serialized));
    }

    @Test
    void updateProfile() {
    }

    @Test
    void getStationForLocation() throws Exception {
        List<GeoResult<POI>> geoResult = List.of(new GeoResult<>(new POI("id", "op", "access",
                new Contact(),
                new Lifetime(),
                "name",
                new Charging(),
                new Location(new Address(), new Position(14.6888899, 49.3950281)),
                "strategy",
                "brand"), new Distance(1)));
        GeoResults<POI> pois = new GeoResults<>(geoResult);
        String serialized = objectMapper.writeValueAsString(pois.getContent());
        when(poiRepository.findByLocationPositionNear(any(Point.class), any(Distance.class))).thenReturn(pois);
        this.mockMvc.perform(get("/api/location")
                        .param("lon", "14.6888899")
                        .param("lat", "49.3950281")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(serialized));
    }

    @Test
    void addFavorite() {
    }

    @Test
    void removeFavorite() {
    }

    @Test
    void getAllFavorites() throws Exception {
//        String profileName = "profile name";
//        Profile profile = new Profile(profileName, List.of(drugstore, cafe));
//
//        String poiId = "poiId";
//        List<POI> pois = List.of(new POI(poiId, "op", "access",
//                new Contact(),
//                new Lifetime(),
//                "name",
//                new Charging(),
//                new Location(new Address(), new Position(14.6888899, 49.3950281)),
//                "strategy",
//                "brand"));
//
//        String deviceName = "device name";
//
//        Favorite favorite = new Favorite(deviceName, profileName, List.of(poiId));
//        List<FavoriteDTO> dto = List.of(new FavoriteDTO(deviceName, profile, pois));
//        String serialized = objectMapper.writeValueAsString(dto);
//
//        when(favoritesRepository.findAll()).thenReturn(List.of(favorite));
//        when(profileRepository.findAll()).thenReturn(List.of(profile));
//        when(poiRepository.findAll()).thenReturn(pois);
//        this.mockMvc.perform(get("/api/favorites"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(serialized));
    }
}