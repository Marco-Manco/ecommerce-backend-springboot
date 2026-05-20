package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.CrearCheckoutDTO;
import com.ecommerce.ecommerce.application.dto.PedidoDTO;
import com.ecommerce.ecommerce.application.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Pedidos y Checkout", description = "Proceso de compra e historial de pedidos")
@PreAuthorize("hasRole('CLIENTE')")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/api/checkout")
    @Operation(summary = "Iniciar checkout: crea pedido, reserva stock y vacía carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado con stock reservado"),
        @ApiResponse(responseCode = "400", description = "Carrito vacío o dirección inválida"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    public ResponseEntity<PedidoDTO> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CrearCheckoutDTO request) {
        PedidoDTO pedido = pedidoService.checkout(userDetails.getUsername(), request.direccionEnvioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/api/pedidos")
    @Operation(summary = "Listar historial de pedidos del usuario")
    public ResponseEntity<Page<PedidoDTO>> listar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarPedidos(userDetails.getUsername(), pageable));
    }

    @GetMapping("/api/pedidos/{id}")
    @Operation(summary = "Ver detalle de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> obtener(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(userDetails.getUsername(), id));
    }
}
