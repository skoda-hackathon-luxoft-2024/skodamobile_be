package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.skoda.dto.LicenceType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "license_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenseDetail {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private String summary;

    @NonNull
    private String impactOfExpiredLicense;

    @NonNull
    private BigDecimal price;

    @NonNull
    private Integer subscriptionPeriod;

    @NonNull
    private LicenceType licenceType;

    @DBRef
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Application> applications = new HashSet<>();

}
