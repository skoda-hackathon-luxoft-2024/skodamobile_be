package com.skoda.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @Schema(description = "Username for authentication", example = "user123", requiredMode = RequiredMode.REQUIRED)
    @NotBlank
    private String username;

    @Schema(description = "Password for authentication", example = "password123", requiredMode = RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}
