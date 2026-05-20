package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Item dentro del carrito de compras")
public record ItemCarritoDTO(
        @Schema(description = "ID del item en el carrito", example = "10")
        Long id,

        @Schema(description = "ID de la variante", example = "5")
        Long varianteProductoId,

        @Schema(description = "SKU de la variante", example = "BUF-ROJ-M")
        String sku,

        @Schema(description = "Nombre del producto", example = "Bufanda de lana")
        String productoNombre,

        @Schema(description = "Descripción de la variante", example = "Rojo - M")
        String variante,

        @Schema(description = "Precio unitario en ARS", example = "1500.00")
        BigDecimal precioUnitario,

        @Schema(description = "Cantidad en el carrito", example = "2")
        Integer cantidad,

        @Schema(description = "Subtotal (precio x cantidad)", example = "3000.00")
        BigDecimal subtotal
) {}
