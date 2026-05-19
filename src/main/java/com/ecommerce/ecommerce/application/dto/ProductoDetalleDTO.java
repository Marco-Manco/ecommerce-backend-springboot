package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Detalle completo de un producto con variantes e imágenes")
public record ProductoDetalleDTO(
        @Schema(description = "ID del producto", example = "1")
        Long id,

        @Schema(description = "Nombre del producto", example = "Bufanda de lana")
        String nombre,

        @Schema(description = "Descripción detallada", example = "Bufanda tejida a mano con lana merino 100%...")
        String descripcion,

        @Schema(description = "Nombre de la categoría", example = "Ropa Tejida")
        String categoria,

        @Schema(description = "Precio desde (variante más barata)", example = "1500.00")
        BigDecimal precioDesde,

        @Schema(description = "Precio hasta (variante más cara)", example = "2000.00")
        BigDecimal precioHasta,

        @Schema(description = "Lista de variantes disponibles")
        List<VarianteDTO> variantes,

        @Schema(description = "Lista de imágenes del producto")
        List<ImagenDTO> imagenes,

        @Schema(description = "Si el producto está activo", example = "true")
        Boolean activo
) {}