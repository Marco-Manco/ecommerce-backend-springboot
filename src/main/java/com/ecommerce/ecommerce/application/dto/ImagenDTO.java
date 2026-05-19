package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Imagen de un producto")
public record ImagenDTO(
        @Schema(description = "ID de la imagen", example = "1")
        Long id,

        @Schema(description = "URL de la imagen en Cloudinary", example = "https://res.cloudinary.com/.../bufanda.jpg")
        String url,

        @Schema(description = "Orden de visualización", example = "1")
        Integer orden
) {}
