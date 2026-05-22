package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.port.out.PagoPort;
import com.ecommerce.ecommerce.application.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Pagos", description = "Confirmación de pagos y webhook de MercadoPago")
public class PagoController {

    private final PedidoService pedidoService;
    private final PagoPort pagoPort;

    public PagoController(PedidoService pedidoService, PagoPort pagoPort) {
        this.pedidoService = pedidoService;
        this.pagoPort = pagoPort;
    }

    @GetMapping("/api/pagos/confirmar")
    @Operation(summary = "Confirma el pago verificando con MercadoPago y redirige a pedidos")
    public ResponseEntity<Void> confirmarRedirect(@RequestParam Long pedidoId) {
        boolean aprobado = pagoPort.verificarPagoAprobado(pedidoId);

        if (aprobado) {
            pedidoService.confirmarPago(pedidoId);
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "http://localhost:5173/pedidos")
                .build();
    }

    @PostMapping("/api/pagos/mercadopago/notify")
    @Operation(summary = "Webhook de MercadoPago. Para usar con ngrok en produccion.")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok().build();
    }
}
