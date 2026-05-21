package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.*;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.ProductoMapper;
import com.ecommerce.ecommerce.domain.model.Categoria;
import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import com.ecommerce.ecommerce.infrastructure.persistence.CategoriaRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.ProductoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.VarianteProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminProductoService {

    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public AdminProductoService(ProductoRepository productoRepository,
                                VarianteProductoRepository varianteProductoRepository,
                                CategoriaRepository categoriaRepository,
                                ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.varianteProductoRepository = varianteProductoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    public ProductoDetalleDTO crearProducto(CrearProductoDTO request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.categoriaId()));

        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);
        return productoMapper.toDetalleDTO(producto);
    }

    public ProductoDetalleDTO actualizarProducto(Long id, ActualizarProductoDTO request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        if (request.nombre() != null) producto.setNombre(request.nombre());
        if (request.descripcion() != null) producto.setDescripcion(request.descripcion());
        if (request.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", request.categoriaId()));
            producto.setCategoria(categoria);
        }
        if (request.activo() != null) producto.setActivo(request.activo());

        producto = productoRepository.save(producto);
        return productoMapper.toDetalleDTO(producto);
    }

    public VarianteDTO agregarVariante(Long productoId, CrearVarianteDTO request) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));

        VarianteProducto variante = new VarianteProducto();
        variante.setProducto(producto);
        variante.setSku(request.sku());
        variante.setColor(request.color());
        variante.setTamanio(request.talle());
        variante.setPrecio(request.precio());
        variante.setStock(request.stock());

        return productoMapper.varianteToDTO(varianteProductoRepository.save(variante));
    }

    public VarianteDTO actualizarVariante(Long varianteId, ActualizarVarianteDTO request) {
        VarianteProducto variante = varianteProductoRepository.findById(varianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Variante", varianteId));

        if (request.precio() != null) variante.setPrecio(request.precio());
        if (request.stock() != null) variante.setStock(request.stock());
        if (request.color() != null) variante.setColor(request.color());
        if (request.talle() != null) variante.setTamanio(request.talle());
        if (request.activo() != null) variante.setActivo(request.activo());

        return productoMapper.varianteToDTO(varianteProductoRepository.save(variante));
    }
}
