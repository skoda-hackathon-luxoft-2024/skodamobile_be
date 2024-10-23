package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.skoda.dto.auth.DeviceType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "vehicles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Vehicle extends User {

    @Indexed(unique = true)
    @NotBlank
    @Field("vin")
    private String username;

    @Indexed(name = "paringNumber_partial", unique = true, partialFilter = "{ 'paringNumber': { $exists: true } }")
    @NonNull
    private String paringNumber;

    @DBRef
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LinkedLicense> linkedLicenses = new HashSet<>();

    @DBRef
    @Builder.Default
    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Mobile> mobile = new HashSet<>();

    public String getVin() {
        return username;
    }

    public void addMobile(Mobile user) {
        mobile.add(user);
    }

    public void removeMobile(Mobile user) {
        mobile.remove(user);
    }

    public void addLicence(LinkedLicense licence) {
        linkedLicenses.add(licence);
    }

    public void extendLicenses(Collection<LinkedLicense> licenses) {
        linkedLicenses.addAll(licenses);
    }

    public void removeLicence(LinkedLicense licence) {
        linkedLicenses.remove(licence);
    }

    public DeviceType getDeviceType() {
        return DeviceType.VEHICLE;
    }
}
