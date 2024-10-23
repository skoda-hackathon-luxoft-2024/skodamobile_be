package com.skoda.service;

import com.skoda.dto.pairing.PairedAccountsDto;
import com.skoda.dto.pairing.ParingNumberDto;

public interface ParingService {

    ParingNumberDto getNumberForPairing(String authorizationHeader);

    void pairMobileWithVehicle(String authorizationHeader, String paringNumber);

    PairedAccountsDto getPairedAccounts(String authorizationHeader);

    void unpair(String authorizationHeader, String accountToRemove);
}
