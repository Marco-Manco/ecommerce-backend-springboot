package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.ActualizarEstadoPedidoDTO;
import com.ecommerce.ecommerce.application.dto.PedidoDTO;
import com.ecommerce.ecommerce.application.service.AdminPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pedidos")
@Tag(name = "Admin - Pedidos", description = "Gestión de todos los pedidos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPedidoController {

    private final AdminPedidoService adminPedidoService;

    public AdminPedidoController(AdminPedidoService adminPedidoService) {
        this.adminPedidoService = adminPedidoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<Page<PedidoDTO>> listar(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(adminPedidoService.listarTodos(pageable));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un pedido")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    public ResponseEntity<PedidoDTO> cambiarEstado(@PathVariable Long id,
                                                    @RequestBody @Valid ActualizarEstadoPedidoDTO request) {
        return ResponseEntity.ok(adminPedidoService.cambiarEstado(id, request));
    }
}
