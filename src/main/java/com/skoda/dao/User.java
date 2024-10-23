package com.skoda.dao;

import com.skoda.dto.auth.DeviceType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class User {

    @Id
    private ObjectId id;

    @NotBlank
    @Indexed(unique = true)
    private String token;

    public abstract DeviceType getDeviceType();

    public abstract String getUsername();

    public Vehicle asVehicle() {
        if (DeviceType.VEHICLE.equals(getDeviceType())) {
            return (Vehicle) this;
        }
        throw new IllegalArgumentException("Unsupported device type: " + getDeviceType());
    }

    public Mobile asMobile() {
        if (DeviceType.MOBILE.equals(getDeviceType())) {
            return (Mobile) this;
        }
        throw new IllegalArgumentException("Unsupported device type: " + getDeviceType());
    }

    public String getPassword() {
        return DeviceType.VEHICLE.equals(getDeviceType())
                ? getUsername()
                : asMobile().getPassword();
    }
}
