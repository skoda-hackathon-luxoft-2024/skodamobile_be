package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenceDetailDto {
    private String id;

    private String name;

    private String description;

    private Integer subscriptionPeriod;

    private LicenceType licenceType;

    private String field1;

    private String field2;

    private String field3;
}
