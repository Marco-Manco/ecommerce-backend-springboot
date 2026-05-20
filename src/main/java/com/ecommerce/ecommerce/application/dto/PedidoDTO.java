package com.ecommerce.ecommerce.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Pedido de compra completo")
public record PedidoDTO(
    @Schema(description = "ID del pedido", example = "1")
    Long id,

    @Schema(description = "Número de pedido legible", example = "ORD-20260519-001")
    String numeroPedido,

    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    String estado,

    @Schema(description = "Subtotal antes de envío", example = "5500.00")
    BigDecimal subtotal,

    @Schema(description = "Costo de envío", example = "0.00")
    BigDecimal costoEnvio,

    @Schema(description = "Total del pedido", example = "5500.00")
    BigDecimal total,

    @Schema(description = "Método de pago", example = "MERCADOPAGO")
    String metodoPago,

    @Schema(description = "Fecha de creación")
    LocalDateTime fechaCreacion,

    @Schema(description = "Fecha de expiración de la reserva de stock")
    LocalDateTime fechaExpiracionReserva,

    @Schema(description = "Items del pedido")
    List<ItemPedidoDTO> items,

    @Schema(description = "Link de pago de MercadoPago", example = "https://www.mercadopago.com.ar/checkout/v1/...")
    String linkPago
) {}
