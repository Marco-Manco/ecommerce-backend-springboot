package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar un producto existente")
public record ActualizarProductoDTO(
    @Size(min = 2, max = 200)
    @Schema(description = "Nuevo nombre del producto", example = "Bufanda de lana merino")
    String nombre,

    @Schema(description = "Nueva descripción", example = "Actualizada...")
    String descripcion,

    @Schema(description = "ID de la nueva categoría", example = "2")
    Long categoriaId,

    @Schema(description = "Activar o desactivar producto", example = "true")
    Boolean activo
) {}
