package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta paginada genérica")
public record PageResponse<T>(
        @Schema(description = "Lista de elementos de la página actual")
        List<T> content,

        @Schema(description = "Número de página actual (0-index)", example = "0")
        int page,

        @Schema(description = "Tamaño de la página", example = "20")
        int size,

        @Schema(description = "Total de elementos en la base de datos", example = "150")
        long totalElements,

        @Schema(description = "Total de páginas", example = "8")
        int totalPages,

        @Schema(description = "Si es la última página", example = "false")
        boolean last
) {
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
