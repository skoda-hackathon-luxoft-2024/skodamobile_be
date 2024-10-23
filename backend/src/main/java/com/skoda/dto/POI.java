package com.skoda.dto;

import com.skoda.dao.Charging;
import com.skoda.dao.Contact;
import com.skoda.dao.Lifetime;
import com.skoda.dao.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
//@Entity(name = "POI")
@Document(collection = "POI")
@AllArgsConstructor
@NoArgsConstructor
public class POI {
    @Id
    private String id;

    private String op_state;
    private String access_type;
    private Contact contact;
    private Lifetime lifetime;
    private String name;
    private Charging charging;
    private Location location;

    private String category;
    private String brand;

}
