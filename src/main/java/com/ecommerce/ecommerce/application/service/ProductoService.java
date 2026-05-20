package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.PageResponse;
import com.ecommerce.ecommerce.application.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.application.dto.ProductoResumenDTO;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.ProductoMapper;
import com.ecommerce.ecommerce.application.port.out.GestionarProductoPort;
import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import com.ecommerce.ecommerce.infrastructure.persistence.ProductoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.VarianteProductoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.specification.ProductoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductoService implements GestionarProductoPort {

    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository,
                           VarianteProductoRepository varianteProductoRepository,
                           ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.varianteProductoRepository = varianteProductoRepository;
        this.productoMapper = productoMapper;
    }

    public PageResponse<ProductoResumenDTO> listar(
            String nombre,
            Long categoriaId,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Pageable pageable) {
        var spec = ProductoSpecification.conFiltros(nombre, categoriaId, precioMin, precioMax);
        Page<Producto> page = productoRepository.findAll(spec, pageable);
        Page<ProductoResumenDTO> dtoPage = page.map(productoMapper::toResumenDTO);
        return PageResponse.from(dtoPage);
    }

    public ProductoDetalleDTO obtenerPorId(Long id) {
        Producto producto = buscarProductoConVariantesEImagenes(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        return productoMapper.toDetalleDTO(producto);
    }

    @Override
    public VarianteProducto buscarVariante(Long varianteId) {
        return varianteProductoRepository.findById(varianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Variante", varianteId));
    }

    @Override
    public Optional<Producto> buscarProductoConVariantesEImagenes(Long productoId) {
        return productoRepository.findByIdWithVariantesAndImagenes(productoId);
    }

    @Override
    @Transactional
    public VarianteProducto lockearVariante(Long varianteId) {
        return varianteProductoRepository.findByIdWithLock(varianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Variante", varianteId));
    }

    @Override
    public List<VarianteProducto> buscarStockBajo(int umbral) {
        return varianteProductoRepository.findStockBajo(umbral);
    }
}
