package com.ecommerce.ecommerce.application.mapper;

import com.ecommerce.ecommerce.application.dto.ImagenDTO;
import com.ecommerce.ecommerce.application.dto.ProductoDetalleDTO;
import com.ecommerce.ecommerce.application.dto.ProductoResumenDTO;
import com.ecommerce.ecommerce.application.dto.VarianteDTO;
import com.ecommerce.ecommerce.domain.model.ImagenProducto;
import com.ecommerce.ecommerce.domain.model.Producto;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(target = "stockDisponible", expression = "java(v.getStockDisponible())")
    @Mapping(target = "talle", source = "tamanio")
    VarianteDTO varianteToDTO(VarianteProducto v);

    List<VarianteDTO> variantesToDTO(List<VarianteProducto> variantes);

    @Mapping(target = "url", expression = "java(optimizarUrl(imagen.getUrl(), \"w_800,h_800,c_fit\"))")
    ImagenDTO imagenToDTO(ImagenProducto imagen);

    List<ImagenDTO> imagenesToDTO(Set<ImagenProducto> imagenes);

    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "precioDesde", expression = "java(precioMinimo(producto))")
    @Mapping(target = "stockTotal", expression = "java(stockTotal(producto))")
    @Mapping(target = "imagenPrincipal", expression = "java(imagenPrincipal(producto))")
    ProductoResumenDTO toResumenDTO(Producto producto);

    List<ProductoResumenDTO> toResumenDTOList(List<Producto> productos);

    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "precioDesde", expression = "java(precioMinimo(producto))")
    @Mapping(target = "precioHasta", expression = "java(precioMaximo(producto))")
    @Mapping(target = "variantes", source = "variantes")
    @Mapping(target = "imagenes", source = "imagenes")
    ProductoDetalleDTO toDetalleDTO(Producto producto);

    default BigDecimal precioMinimo(Producto producto) {
        return producto.getVariantes().stream()
                .filter(VarianteProducto::getActivo)
                .map(VarianteProducto::getPrecio)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    default BigDecimal precioMaximo(Producto producto) {
        return producto.getVariantes().stream()
                .filter(VarianteProducto::getActivo)
                .map(VarianteProducto::getPrecio)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    default Integer stockTotal(Producto producto) {
        return producto.getVariantes().stream()
                .filter(VarianteProducto::getActivo)
                .mapToInt(VarianteProducto::getStockDisponible)
                .sum();
    }
    default String imagenPrincipal(Producto producto) {
        return producto.getImagenes().stream()
                .filter(i -> i.getOrden() != null)
                .min(Comparator.comparing(ImagenProducto::getOrden))
                .map(ImagenProducto::getUrl)
                .map(url -> optimizarUrl(url, "w_400,h_400,c_fill,g_auto"))
                .orElse(null);
    }

    default String optimizarUrl(String url, String transformacion) {
        if (url == null || !url.contains("cloudinary")) return url;
        return url.replaceFirst("/upload/", "/upload/" + transformacion + "/");
    }
}
