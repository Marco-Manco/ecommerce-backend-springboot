package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.PageResponse;
import com.ecommerce.ecommerce.application.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.application.dto.ProductoResumenDTO;
import com.ecommerce.ecommerce.application.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo público de productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos con filtros y paginación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado de productos")
    })
    public ResponseEntity<PageResponse<ProductoResumenDTO>> listar(
            @Parameter(description = "Buscar por nombre (contiene)")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por categoría")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Precio mínimo")
            @RequestParam(required = false) BigDecimal precioMin,
            @Parameter(description = "Precio máximo")
            @RequestParam(required = false) BigDecimal precioMax,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(
                productoService.listar(nombre, categoriaId, precioMin, precioMax, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDetalleDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }
}
