package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenseRenewal {
    private String id;
    private String name;
    private String message;
    private Boolean isSuccess;
}
