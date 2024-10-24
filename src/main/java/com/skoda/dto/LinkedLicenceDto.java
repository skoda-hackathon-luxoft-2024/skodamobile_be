package com.skoda.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkedLicenceDto {
    private String id;

    private String name;

    private String description;

    private String summary;

    private String impactOfExpiredLicense;

    private BigDecimal netPrice;

    private BigDecimal vatPercent;

    private BigDecimal vat;

    private String vin;

    private Instant purchaseDate;

    private Integer subscriptionPeriod;

    private LicenceType licenceType;

    private int discountPercent;

    private BigDecimal discountNetPrice;

    private String currency;

    @JsonGetter
    public Instant nextPushNotification() {
        if (!SubscriptionStatus.ACTIVE.equals(status())) {
            ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault());
            ZonedDateTime newDateTime = zonedDateTime.plusSeconds(30);
            return newDateTime.toInstant();
        }
        return null;
    }

    @JsonGetter
    public Instant expirationDate() {
        if (purchaseDate != null) {
            ZonedDateTime zonedDateTime = purchaseDate.atZone(ZoneId.systemDefault());
            ZonedDateTime newDateTime = zonedDateTime.plusMonths(subscriptionPeriod);
            return newDateTime.toInstant();
        }
        return null;
    }

    @JsonGetter
    public SubscriptionStatus status() {
        if (LicenceType.FREE.equals(licenceType)) {
            return SubscriptionStatus.ACTIVE;
        }

        Instant expirationDate = expirationDate();
        if (expirationDate != null) {
            return Instant.now().isBefore(expirationDate)
                    ? SubscriptionStatus.ACTIVE
                    : SubscriptionStatus.EXPIRED;
        }

        return SubscriptionStatus.INACTIVE;
    }
}
