package com.skoda.service;

import com.skoda.dao.User;
import com.skoda.dto.auth.AuthResponseDto;
import com.skoda.dto.auth.DeviceType;
import com.skoda.dto.auth.LoginDto;
import com.skoda.dto.auth.ValidationTokenResponseDto;

public interface AuthService {

    AuthResponseDto loginVehicle(String vin);

    AuthResponseDto loginMobile(LoginDto loginDto);

    ValidationTokenResponseDto validateToken(String token, DeviceType deviceType);

    User getUserByToken(String authorizationHeader);

    User getUserByUserName(String username);
}