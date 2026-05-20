package com.ecommerce.ecommerce.application.port.out;

import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;

import java.util.Optional;

public interface GestionarProductoPort {
    VarianteProducto buscarVariante(Long varianteId);
    Optional<Producto> buscarProductoConVariantesEImagenes(Long productoId);
}
