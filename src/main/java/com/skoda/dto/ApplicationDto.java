package com.skoda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDto {
    private String id;

    private String licence;

    private String name;

    private String description;

    private String field1;

    private String field2;

    private String field3;
}
