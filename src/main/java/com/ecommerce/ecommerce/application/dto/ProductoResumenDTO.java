package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resumen de producto para listados paginados")
public record ProductoResumenDTO(
        @Schema(description = "ID del producto", example = "1")
        Long id,

        @Schema(description = "Nombre del producto", example = "Bufanda de lana")
        String nombre,

        @Schema(description = "Descripción corta", example = "Tejida a mano con lana")
        String descripcion,

        @Schema(description = "Nombre de la categoria", example = "Ropa Tejida")
        String categoria,

        @Schema(description = "Precio desde(la variante mas barata", example = "1500")
        BigDecimal precioDesde,

        @Schema(description = "Stock total disponible(suma de todas las variantes)", example = "8")
        Integer stockTotal,

        @Schema(description = "Stock total disponible(suma de todas las variantes)", example = "https://res.cloudinary.com/.../bufanda.jpg")
        String imagenPrincipal,

        @Schema(description = "Si el producto está activo", example = "true")
        Boolean activo
) {}
