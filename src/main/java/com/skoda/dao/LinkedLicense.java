package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Document(collection = "linked_licenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkedLicense {
    @Id
    private ObjectId id;

    @DBRef
    @NonNull
    private LicenseDetail licence;

    @DBRef
    @NonNull
    @EqualsAndHashCode.Exclude
    private Vehicle vehicle;

    @Nullable
    private Instant purchaseDate;

    @Builder.Default
    @ToString.Exclude
    private Set<Instant> subscriptionRenewalAttempts = new TreeSet<>(Comparator.reverseOrder());

    public String getId() {
        return licence.getId().toHexString();
    }

    public void addSubscriptionRenewalAttempt(Instant attempt) {
        subscriptionRenewalAttempts.add(attempt);
    }
}