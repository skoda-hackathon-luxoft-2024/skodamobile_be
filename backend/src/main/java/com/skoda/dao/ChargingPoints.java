package com.skoda.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargingPoints {
    private String op_state;
    private String evse_id;
    private List<String> connectors;
    private List<PowerSpecs> powerSpecs;

    //    @DateTimeFormat(iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME)
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]X")
    private String live_availability_last_modification; //2022-06-02T09:17:42.204000+00:00
    private Boolean is_cable_attached;
    private String current_type;
}
