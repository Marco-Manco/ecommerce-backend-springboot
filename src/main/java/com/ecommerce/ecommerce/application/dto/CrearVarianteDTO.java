package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Datos para agregar una variante a un producto")
public record CrearVarianteDTO(
    @NotBlank
    @Schema(description = "SKU único", example = "BUF-LAN-VER-M")
    String sku,

    @Schema(description = "Color", example = "Verde")
    String color,

    @Schema(description = "Talle", example = "M")
    String talle,

    @NotNull @Positive
    @Schema(description = "Precio en ARS", example = "1500.00")
    BigDecimal precio,

    @NotNull @Positive
    @Schema(description = "Stock inicial", example = "10")
    Integer stock
) {}
