package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para agregar un producto al carrito")
public record AgregarItemCarritoDTO(
        @NotNull
        @Schema(description = "ID de la variante del producto", example = "5")
        Long varianteProductoId,

        @Min(1)
        @Schema(description = "Cantidad a agregar", example = "2")
        Integer cantidad
) {}
