package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalizedData {
    private Integer discountPercent;
    private Integer subscriptionRenewalAttempts;
}
