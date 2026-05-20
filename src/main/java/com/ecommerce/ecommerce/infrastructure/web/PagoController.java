package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Pagos", description = "Confirmación de pagos y webhook de MercadoPago")
public class PagoController {

    private final PedidoService pedidoService;

    public PagoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/api/pagos/confirmar")
    @Operation(summary = "Confirma el pago de un pedido (redirect de MP o manual)")
    public ResponseEntity<Map<String, String>> confirmarRedirect(@RequestParam Long pedidoId) {
        pedidoService.confirmarPago(pedidoId);
        return ResponseEntity.ok(Map.of("estado", "confirmado", "pedidoId", pedidoId.toString()));
    }

    @PostMapping("/api/pagos/mercadopago/notify")
    @Operation(summary = "Webhook de MercadoPago. Para usar con ngrok en produccion.")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> body) {
        // Para desarrollo local: el redirect GET /api/pagos/confirmar
        // ya confirma el pago. El webhook es para produccion con ngrok.
        return ResponseEntity.ok().build();
    }
}
