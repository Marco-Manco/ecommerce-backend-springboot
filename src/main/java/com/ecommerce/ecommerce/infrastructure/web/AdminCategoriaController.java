package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.ActualizarCategoriaDTO;
import com.ecommerce.ecommerce.application.dto.CategoriaDTO;
import com.ecommerce.ecommerce.application.dto.CrearCategoriaDTO;
import com.ecommerce.ecommerce.application.service.AdminCategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
@Tag(name = "Admin - Categorías", description = "Gestión de categorías de productos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoriaController {

    private final AdminCategoriaService adminCategoriaService;

    public AdminCategoriaController(AdminCategoriaService adminCategoriaService) {
        this.adminCategoriaService = adminCategoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorías")
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(adminCategoriaService.listarTodas());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva categoría")
    @ApiResponse(responseCode = "201", description = "Categoría creada")
    public ResponseEntity<CategoriaDTO> crear(@RequestBody @Valid CrearCategoriaDTO request) {
        CategoriaDTO categoria = adminCategoriaService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(categoria.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoría")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id,
                                                    @RequestBody @Valid ActualizarCategoriaDTO request) {
        return ResponseEntity.ok(adminCategoriaService.actualizar(id, request));
    }
}
