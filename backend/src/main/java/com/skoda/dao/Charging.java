package com.skoda.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Charging {
    private List<NameId> mobility_service_providers;
    private NameId charge_point_operator;
    private List<ChargingDevices> chargingDevices;
}
