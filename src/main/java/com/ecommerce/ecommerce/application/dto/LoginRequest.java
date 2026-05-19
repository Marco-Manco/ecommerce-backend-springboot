package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales de inicio de sesión")
public record LoginRequest(
        @NotBlank @Email
        @Schema(description = "Correo electrónico", example = "maria@gmail.com")
        String email,
        @NotBlank
        @Schema(description = "Contraseña", example = "miPassword123")
        String password
) {}
