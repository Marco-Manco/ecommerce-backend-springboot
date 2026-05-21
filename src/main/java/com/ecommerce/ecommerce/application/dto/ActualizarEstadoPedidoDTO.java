package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para cambiar el estado de un pedido")
public record ActualizarEstadoPedidoDTO(
    @NotBlank
    @Schema(description = "Nuevo estado del pedido", example = "EN_PREPARACION",
            allowableValues = {"PAGADO", "EN_PREPARACION", "ENVIADO", "ENTREGADO", "CANCELADO"})
    String estado
) {}
