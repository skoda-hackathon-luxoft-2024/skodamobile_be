package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @Id
    private String name;

    @DBRef
    private Profile profile;

    @DBRef
    private Set<POI> favorites;

    public Device addFavorite(POI poi) {
        favorites.add(poi);
        return this;
    }

    public Device removeFavorite(POI poi) {
        favorites.remove(poi);
        return this;
    }
}
