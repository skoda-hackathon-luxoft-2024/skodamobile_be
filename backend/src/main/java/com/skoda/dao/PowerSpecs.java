package com.skoda.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerSpecs {
    private Integer nominal_amperage;
    private Integer nominal_voltage;
    private Double nominal_power_output;
}
