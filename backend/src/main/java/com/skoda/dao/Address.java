package com.skoda.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String zipcode;
    private String country;
    private String country_code;
    private String language_code;
    private String city;
    private String street;
    private String state;
}
