package com.skoda.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lifetime {
    private String last_update_static_information; //: "2021-11-17T18:50:46+00:00",
    private String last_update_live_information; //": "2021-10-28T12:50:25+00:00",
    private List<String> targets;
    private List<DataSources> data_sources;
}
