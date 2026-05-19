package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación exitosa")
public record AuthResponse(
        @Schema(description = "Token JWT para usar en Authorization header", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        @Schema(description = "Email del usuario autenticado", example = "maria@gmail.com")
        String email,

        @Schema(description = "Nombre del usuario", example = "María García")
        String nombre,

        @Schema(description = "Rol del usuario", example = "CLIENTE")
        String rol
) {
    public AuthResponse(String token, String email, String nombre, String rol) {
        this(token, "Bearer", email, nombre, rol);
    }
}
