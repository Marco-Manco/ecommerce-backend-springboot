package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "Datos para actualizar una variante existente")
public record ActualizarVarianteDTO(
    @Schema(description = "Nuevo precio", example = "1600.00")
    @Positive
    BigDecimal precio,

    @Schema(description = "Nuevo stock total", example = "15")
    @PositiveOrZero
    Integer stock,

    @Schema(description = "Color actualizado", example = "Verde")
    String color,

    @Schema(description = "Talle actualizado", example = "L")
    String talle,

    @Schema(description = "Activar o desactivar", example = "true")
    Boolean activo
) {}
