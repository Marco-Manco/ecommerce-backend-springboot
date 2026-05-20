package com.ecommerce.ecommerce.application.mapper;

import com.ecommerce.ecommerce.application.dto.CarritoDTO;
import com.ecommerce.ecommerce.application.dto.ItemCarritoDTO;
import com.ecommerce.ecommerce.domain.model.Carrito;
import com.ecommerce.ecommerce.domain.model.ItemCarrito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CarritoMapper {
    @Mapping(target = "varianteProductoId", source = "varianteProducto.id")
    @Mapping(target = "sku", source = "varianteProducto.sku")
    @Mapping(target = "productoNombre", source = "varianteProducto.producto.nombre")
    @Mapping(target = "variante", expression = "java(varianteDescripcion(item))")
    @Mapping(target = "precioUnitario", source = "varianteProducto.precio")
    @Mapping(target = "subtotal", expression = "java(item.getVarianteProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))")
    ItemCarritoDTO itemToDTO(ItemCarrito item);

    List<ItemCarritoDTO> itemsToDTO(List<ItemCarrito> items);

    @Mapping(target = "carritoId", source = "id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalItems", expression = "java(carrito.getItems().stream().mapToInt(ItemCarrito::getCantidad).sum())")
    @Mapping(target = "total", expression = "java(calcularTotal(carrito))")
    CarritoDTO toDTO(Carrito carrito);

    default String varianteDescripcion(ItemCarrito item) {
        var v = item.getVarianteProducto();
        if (v.getColor() != null && v.getTamanio() != null) {
            return v.getColor() + " - " + v.getTamanio();
        }
        if (v.getColor() != null) return v.getColor();
        if (v.getTamanio() != null) return v.getTamanio();
        return null;
    }
    default BigDecimal calcularTotal(Carrito carrito) {
        return carrito.getItems().stream()
                .map(i -> i.getVarianteProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
