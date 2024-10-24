package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenceDetailDto {
    private String id;

    private String name;

    private String description;

    private String summary;

    private String impactOfExpiredLicense;

    private BigDecimal netPrice;

    private BigDecimal vatPercent;

    private Integer subscriptionPeriod;

    private LicenceType licenceType;

    private String currency;
}
