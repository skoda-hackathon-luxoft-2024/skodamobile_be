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

    public int incrementAndGet() {
        return subscriptionRenewalAttempts.incrementAndGet();
    }

    public int getSubscriptionRenewalAttempts() {
        return subscriptionRenewalAttempts.get();
    }

    public int discountPercent() {
        int discountPercent = 0;
        if (getSubscriptionRenewalAttempts() > 10) {
            discountPercent = 15;
        }
        if (getSubscriptionRenewalAttempts() > 50) {
            discountPercent = 30;
        }
        if (getSubscriptionRenewalAttempts() > 100) {
            discountPercent = 50;
        }
        return discountPercent;
    }

    public String getId() {
        return licence.getId().toHexString();
    }
}