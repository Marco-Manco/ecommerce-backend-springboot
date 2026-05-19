package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Respuesta de error estándar")
public record ErrorResponse(
        @Schema(description = "Mensaje de error legible", example = "Producto con ID 999 no encontrado")
        String mensaje,

        @Schema(description = "Código de estado HTTP", example = "404")
        int status,

        @Schema(description = "Timestamp del error")
        LocalDateTime timestamp,

        @Schema(description = "Errores de validación por campo (opcional)")
        Map<String, String> errores
) {
    public ErrorResponse(String mensaje, int status) {
        this(mensaje, status, LocalDateTime.now(), null);
    }
    public ErrorResponse(String mensaje, int status, Map<String, String> errores) {
        this(mensaje, status, LocalDateTime.now(), errores);
    }
}
