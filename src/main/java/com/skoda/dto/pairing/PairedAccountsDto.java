package com.skoda.dto.pairing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PairedAccountsDto {
    private List<String> accounts;
}
