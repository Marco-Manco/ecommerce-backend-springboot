package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.AgregarItemCarritoDTO;
import com.ecommerce.ecommerce.application.dto.CarritoDTO;
import com.ecommerce.ecommerce.application.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
@PreAuthorize("hasRole('CLIENTE')")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @Operation(summary = "Obtener el carrito del usuario")
    @ApiResponse(responseCode = "200", description = "Carrito con items y totales")
    public ResponseEntity<CarritoDTO> obtener(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(userDetails.getUsername()));
    }

    @PostMapping("/items")
    @Operation(summary = "Agregar un item al carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item agregado"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    public ResponseEntity<CarritoDTO> agregarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AgregarItemCarritoDTO request) {
        return ResponseEntity.ok(carritoService.agregarItem(userDetails.getUsername(), request));
    }

    @PatchMapping("/items/{id}")
    @Operation(summary = "Actualizar cantidad de un item")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam int cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(userDetails.getUsername(), id, cantidad));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Eliminar un item del carrito")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(carritoService.eliminarItem(userDetails.getUsername(), id));
    }

    @DeleteMapping
    @Operation(summary = "Vaciar el carrito completamente")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    public ResponseEntity<Void> vaciar(@AuthenticationPrincipal UserDetails userDetails) {
        carritoService.vaciarCarrito(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
