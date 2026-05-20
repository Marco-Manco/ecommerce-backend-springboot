package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para iniciar el checkout")
public record CrearCheckoutDTO(
    @NotNull
    @Schema(description = "ID de la dirección de envío", example = "3")
    Long direccionEnvioId
) {}
