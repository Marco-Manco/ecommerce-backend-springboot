package com.ecommerce.ecommerce.application.port.out;

import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;

import java.util.List;
import java.util.Optional;

public interface GestionarProductoPort {
    VarianteProducto buscarVariante(Long varianteId);
    VarianteProducto lockearVariante(Long varianteId);
    List<VarianteProducto> buscarStockBajo(int umbral);
    Optional<Producto> buscarProductoConVariantesEImagenes(Long productoId);
}
