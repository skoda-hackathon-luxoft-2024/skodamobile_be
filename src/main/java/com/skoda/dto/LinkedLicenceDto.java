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

    private BigDecimal fullPrice;

    private String vin;

    private Instant purchaseDate;

    private Integer subscriptionPeriod;

    private LicenceType licenceType;

    private int discountPercent;

    @JsonGetter
    public BigDecimal discountPrice() {
        BigDecimal percentage = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal amountToSubtract = fullPrice.multiply(percentage);
        BigDecimal result = fullPrice.subtract(amountToSubtract);
        return result.setScale(0, RoundingMode.HALF_UP);
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
