package com.ecommerce.ecommerce.application.mapper;

import com.ecommerce.ecommerce.application.dto.ItemPedidoDTO;
import com.ecommerce.ecommerce.application.dto.PedidoDTO;
import com.ecommerce.ecommerce.domain.model.ItemCarrito;
import com.ecommerce.ecommerce.domain.model.ItemPedido;
import com.ecommerce.ecommerce.domain.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "estado", expression = "java(pedido.getEstado().name())")
    @Mapping(target = "metodoPago", expression = "java(pedido.getMetodoPago().name())")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "linkPago", ignore = true)
    PedidoDTO toDTO(Pedido pedido);

    default PedidoDTO toDTO(Pedido pedido, String linkPago) {
        PedidoDTO base = toDTO(pedido);
        return new PedidoDTO(base.id(), base.numeroPedido(), base.estado(),
                base.subtotal(), base.costoEnvio(), base.total(), base.metodoPago(),
                base.fechaCreacion(), base.fechaExpiracionReserva(), base.items(), linkPago);
    }

    ItemPedidoDTO itemToDTO(ItemPedido item);

    List<PedidoDTO> toDTOList(List<Pedido> pedidos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "varianteProducto", source = "varianteProducto")
    @Mapping(target = "productoNombre", source = "varianteProducto.producto.nombre")
    @Mapping(target = "varianteDescripcion", expression = "java(descripcion(item))")
    @Mapping(target = "precioUnitario", source = "varianteProducto.precio")
    @Mapping(target = "subtotal", expression = "java(item.getVarianteProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))")
    ItemPedido carritoItemToItemPedido(ItemCarrito item);

    default String descripcion(ItemCarrito item) {
        var v = item.getVarianteProducto();
        if (v.getColor() != null && v.getTamanio() != null) return v.getColor() + " - " + v.getTamanio();
        if (v.getColor() != null) return v.getColor();
        if (v.getTamanio() != null) return v.getTamanio();
        return null;
    }
}
