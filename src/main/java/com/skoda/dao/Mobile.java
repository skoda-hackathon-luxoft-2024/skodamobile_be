package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.skoda.dto.auth.DeviceType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "mobile_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Mobile extends User {

    @Indexed(unique = true)
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public DeviceType getDeviceType() {
        return DeviceType.MOBILE;
    }

    @DBRef
    @Builder.Default
    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Vehicle> vehicles = new HashSet<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }
}
