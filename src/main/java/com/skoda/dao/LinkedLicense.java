package com.skoda.dao;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

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
    private AtomicInteger subscriptionRenewalAttempts = new AtomicInteger(0);
//    private Set<Instant> subscriptionRenewalAttempts = new TreeSet<>(Comparator.reverseOrder());
//    public void addSubscriptionRenewalAttempt(Instant attempt) {
//        subscriptionRenewalAttempts.add(attempt);
//    }

    public int incrementAndGet() {
        return subscriptionRenewalAttempts.incrementAndGet();
    }

    public int getSubscriptionRenewalAttempts() {
        return subscriptionRenewalAttempts.get();
    }

    public String getId() {
        return licence.getId().toHexString();
    }
}