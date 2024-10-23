package com.skoda.dao;

import com.skoda.dto.POI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Root {
    private List<POI> pois;
    private Paging paging;
}
