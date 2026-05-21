package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.ActualizarEstadoPedidoDTO;
import com.ecommerce.ecommerce.application.dto.PedidoDTO;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.PedidoMapper;
import com.ecommerce.ecommerce.domain.enums.EstadoPedido;
import com.ecommerce.ecommerce.domain.model.ItemPedido;
import com.ecommerce.ecommerce.domain.model.Pedido;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import com.ecommerce.ecommerce.infrastructure.persistence.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminPedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public AdminPedidoService(PedidoRepository pedidoRepository,
                              PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarTodos(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(pedidoMapper::toDTO);
    }

    public PedidoDTO cambiarEstado(Long pedidoId, ActualizarEstadoPedidoDTO request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        EstadoPedido nuevoEstado = EstadoPedido.valueOf(request.estado().toUpperCase());

        if (nuevoEstado == EstadoPedido.CANCELADO && pedido.getEstado() == EstadoPedido.PENDIENTE) {
            for (ItemPedido item : pedido.getItems()) {
                VarianteProducto variante = item.getVarianteProducto();
                if (variante != null) {
                    variante.setStockReservado(variante.getStockReservado() - item.getCantidad());
                }
            }
        }

        if (nuevoEstado == EstadoPedido.ENVIADO) {
            pedido.setFechaEnvio(java.time.LocalDateTime.now());
        }

        pedido.setEstado(nuevoEstado);
        return pedidoMapper.toDTO(pedido);
    }
}
