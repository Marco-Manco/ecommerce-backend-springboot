package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Carrito de compras del usuario")
public record CarritoDTO(
        @Schema(description = "ID del carrito", example = "1")
        Long carritoId,

        @Schema(description = "Items en el carrito")
        List<ItemCarritoDTO> items,

        @Schema(description = "Cantidad total de productos", example = "3")
        int totalItems,

        @Schema(description = "Total en ARS", example = "5500.00")
        BigDecimal total
) {}