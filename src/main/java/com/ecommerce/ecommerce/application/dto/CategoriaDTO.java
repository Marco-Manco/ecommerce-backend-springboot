package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Categoría de productos, con subcategorías anidadas")
public record CategoriaDTO(
        @Schema(description = "ID de la categoría", example = "1")
        Long id,

        @Schema(description = "Nombre de la categoría", example = "Ropa Tejida")
        String nombre,

        @Schema(description = "Descripción de la categoría", example = "Prendas tejidas a mano")
        String descripcion,

        @Schema(description = "Subcategorías hijas")
        List<CategoriaDTO> subcategorias
) {}
