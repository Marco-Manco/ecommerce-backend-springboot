package com.ecommerce.ecommerce.infrastructure.web;

import com.ecommerce.ecommerce.application.dto.*;
import com.ecommerce.ecommerce.application.port.out.AlmacenamientoPort;
import com.ecommerce.ecommerce.application.service.AdminProductoService;
import com.ecommerce.ecommerce.domain.model.ImagenProducto;
import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.infrastructure.persistence.ImagenProductoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/admin/productos")
@Tag(name = "Admin - Productos", description = "Gestión de productos, variantes y stock")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoController {

    private final AdminProductoService adminProductoService;
    private final AlmacenamientoPort almacenamientoPort;
    private final ProductoRepository productoRepository;
    private final ImagenProductoRepository imagenProductoRepository;

    public AdminProductoController(AdminProductoService adminProductoService,
                                   AlmacenamientoPort almacenamientoPort,
                                   ProductoRepository productoRepository,
                                   ImagenProductoRepository imagenProductoRepository) {
        this.adminProductoService = adminProductoService;
        this.almacenamientoPort = almacenamientoPort;
        this.productoRepository = productoRepository;
        this.imagenProductoRepository = imagenProductoRepository;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductoDetalleDTO> crear(@RequestBody @Valid CrearProductoDTO request) {
        ProductoDetalleDTO producto = adminProductoService.crearProducto(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(producto.id()).toUri();
        return ResponseEntity.created(location).body(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    public ResponseEntity<ProductoDetalleDTO> actualizar(@PathVariable Long id,
                                                          @RequestBody @Valid ActualizarProductoDTO request) {
        return ResponseEntity.ok(adminProductoService.actualizarProducto(id, request));
    }

    @PostMapping("/{id}/variantes")
    @Operation(summary = "Agregar una variante a un producto")
    @ApiResponse(responseCode = "201", description = "Variante creada")
    public ResponseEntity<VarianteDTO> agregarVariante(@PathVariable Long id,
                                                        @RequestBody @Valid CrearVarianteDTO request) {
        VarianteDTO variante = adminProductoService.agregarVariante(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{varianteId}").buildAndExpand(variante.id()).toUri();
        return ResponseEntity.created(location).body(variante);
    }

    @PutMapping("/variantes/{id}")
    @Operation(summary = "Actualizar stock, precio, color o talle de una variante")
    public ResponseEntity<VarianteDTO> actualizarVariante(@PathVariable Long id,
                                                           @RequestBody @Valid ActualizarVarianteDTO request) {
        return ResponseEntity.ok(adminProductoService.actualizarVariante(id, request));
    }

    @PostMapping("/{id}/imagenes")
    @Operation(summary = "Subir una imagen para un producto (máximo 3)")
    @ApiResponse(responseCode = "201", description = "Imagen subida")
    public ResponseEntity<ImagenDTO> subirImagen(@PathVariable Long id,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException("Producto", id));

        String url = almacenamientoPort.subirImagen(file.getBytes(), producto.getId() + "_" + System.currentTimeMillis());

        ImagenProducto imagen = new ImagenProducto();
        imagen.setProducto(producto);
        imagen.setUrl(url);
        imagen.setOrden(imagenProductoRepository.findByProductoIdOrderByOrdenAsc(producto.getId()).size() + 1);
        imagen = imagenProductoRepository.save(imagen);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ImagenDTO(imagen.getId(), imagen.getUrl(), imagen.getOrden()));
    }

    @DeleteMapping("/imagenes/{id}")
    @Operation(summary = "Eliminar una imagen de un producto")
    @ApiResponse(responseCode = "204", description = "Imagen eliminada")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id) {
        ImagenProducto imagen = imagenProductoRepository.findById(id)
                .orElseThrow(() -> new com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException("Imagen", id));

        String publicId = imagen.getUrl().substring(imagen.getUrl().lastIndexOf("/") + 1).split("\\.")[0];
        almacenamientoPort.eliminarImagen("ecommerce/" + publicId);

        imagenProductoRepository.delete(imagen);
        return ResponseEntity.noContent().build();
    }
}
