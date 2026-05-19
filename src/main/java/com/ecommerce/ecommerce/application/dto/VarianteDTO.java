package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Variante de un producto(color, talle, precio, stock")
public record VarianteDTO(
        @Schema(description = "ID de la varinte", example = "10")
        Long id,

        @Schema(description = "SKU único", example = "BUF-ROJ-M")
        String sku,

        @Schema(description = "Color", example = "Rojo")
        String color,

        @Schema(description = "Talle", example = "M")
        String talle,

        @Schema(description = "Precio en ARS", example = "1500.00")
        BigDecimal precio,

        @Schema(description = "Stock disponible(stock total - stock reservado")
        Integer stockDisponible
) {}
