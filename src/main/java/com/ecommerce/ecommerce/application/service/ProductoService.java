package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.PageResponse;
import com.ecommerce.ecommerce.application.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.application.dto.ProductoResumenDTO;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.ProductoMapper;
import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.infrastructure.persistence.ProductoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.specification.ProductoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public  ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper){
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    public PageResponse<ProductoResumenDTO> listar(
            String nombre,
            Long categoriaId,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Pageable pageable
    ){
        var spec = ProductoSpecification.conFiltros(nombre, categoriaId, precioMin, precioMax);
        Page<Producto> page = productoRepository.findAll(spec, pageable);
        Page<ProductoResumenDTO> dtoPage = page.map(productoMapper::toResumenDTO);
        return PageResponse.from(dtoPage);
    }

    public ProductoDetalleDTO obtenerPorId(Long id){
        Producto producto = productoRepository.findByIdWithVariantesAndImagenes(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        return productoMapper.toDetalleDTO(producto);
    }
}
