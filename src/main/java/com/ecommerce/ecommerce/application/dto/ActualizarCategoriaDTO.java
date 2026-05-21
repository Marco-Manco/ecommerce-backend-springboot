package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar una categoría existente")
public record ActualizarCategoriaDTO(
    @Size(min = 2, max = 100)
    @Schema(description = "Nuevo nombre", example = "Ropa de costura")
    String nombre,

    @Schema(description = "Nueva descripción")
    String descripcion,

    @Schema(description = "Nueva categoría padre", example = "1")
    Long categoriaPadreId,

    @Schema(description = "Activar o desactivar", example = "true")
    Boolean activo
) {}
