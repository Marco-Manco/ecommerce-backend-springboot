package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear una nueva categoría")
public record CrearCategoriaDTO(
    @NotBlank @Size(min = 2, max = 100)
    @Schema(description = "Nombre de la categoría", example = "Ropa de costura")
    String nombre,

    @Schema(description = "Descripción", example = "Prendas confeccionadas con máquina de coser")
    String descripcion,

    @Schema(description = "ID de la categoría padre (para subcategorías)", example = "1")
    Long categoriaPadreId
) {}
