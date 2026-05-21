package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear un nuevo producto con sus variantes iniciales")
public record CrearProductoDTO(
    @NotBlank @Size(min = 2, max = 200)
    @Schema(description = "Nombre del producto", example = "Bufanda de lana")
    String nombre,

    @Schema(description = "Descripción del producto", example = "Tejida a mano...")
    String descripcion,

    @NotNull
    @Schema(description = "ID de la categoría", example = "2")
    Long categoriaId
) {}
