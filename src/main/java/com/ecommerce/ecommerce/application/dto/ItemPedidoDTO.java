package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Item dentro de un pedido (snapshot del producto al comprar)")
public record ItemPedidoDTO(
    @Schema(description = "ID del item", example = "1")
    Long id,

    @Schema(description = "Nombre del producto al momento de la compra", example = "Bufanda de lana")
    String productoNombre,

    @Schema(description = "Descripción de la variante", example = "Rojo - M")
    String varianteDescripcion,

    @Schema(description = "Cantidad comprada", example = "2")
    Integer cantidad,

    @Schema(description = "Precio unitario al momento de la compra", example = "1500.00")
    BigDecimal precioUnitario,

    @Schema(description = "Subtotal del item", example = "3000.00")
    BigDecimal subtotal
) {}
