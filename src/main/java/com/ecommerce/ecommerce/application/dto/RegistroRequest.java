package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un nuevo usuario")
public record RegistroRequest(
        @NotBlank @Size(min = 2, max = 100)
        @Schema(description = "Nombre completo", example = "Juan Garcia")
        String nombre,

        @NotBlank @Email
        @Schema(description = "Correo electrónico", example = "juan@gmail.com")
        String email,

        @NotBlank @Size(min = 6, max = 100)
        @Schema(description = "Contraseña (minimo 6 caracteres)", example = "miPassword123")
        String password

) {
}
