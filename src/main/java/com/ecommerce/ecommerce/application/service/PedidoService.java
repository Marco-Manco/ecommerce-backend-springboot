package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.PedidoDTO;
import com.ecommerce.ecommerce.application.exception.RecursoNoEncontradoException;
import com.ecommerce.ecommerce.application.mapper.PedidoMapper;
import com.ecommerce.ecommerce.application.port.out.BuscarUsuarioPort;
import com.ecommerce.ecommerce.application.port.out.GestionarCarritoPort;
import com.ecommerce.ecommerce.application.port.out.GestionarProductoPort;
import com.ecommerce.ecommerce.application.port.out.PagoPort;
import com.ecommerce.ecommerce.domain.enums.EstadoPago;
import com.ecommerce.ecommerce.domain.enums.EstadoPedido;
import com.ecommerce.ecommerce.domain.exception.StockInsuficienteException;
import com.ecommerce.ecommerce.domain.model.*;
import com.ecommerce.ecommerce.infrastructure.persistence.PagoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final BuscarUsuarioPort buscarUsuarioPort;
    private final GestionarCarritoPort gestionarCarritoPort;
    private final GestionarProductoPort gestionarProductoPort;
    private final PagoPort pagoPort;
    private final PedidoMapper pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository,
                         PagoRepository pagoRepository,
                         BuscarUsuarioPort buscarUsuarioPort,
                         GestionarCarritoPort gestionarCarritoPort,
                         GestionarProductoPort gestionarProductoPort,
                         PagoPort pagoPort,
                         PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.buscarUsuarioPort = buscarUsuarioPort;
        this.gestionarCarritoPort = gestionarCarritoPort;
        this.gestionarProductoPort = gestionarProductoPort;
        this.pagoPort = pagoPort;
        this.pedidoMapper = pedidoMapper;
    }

    public PedidoDTO checkout(String email, Long direccionEnvioId) {
        Carrito carrito = obtenerCarritoValidado(email);
        Direccion direccion = validarDireccion(carrito.getUsuario(), direccionEnvioId);
        lockearYValidarStock(carrito.getItems());
        Pedido pedido = crearPedidoConItems(carrito, direccion);
        pedidoRepository.save(pedido);
        gestionarCarritoPort.vaciar(email);
        String linkPago = pagoPort.generarLinkPago(pedido);
        return pedidoMapper.toDTO(pedido, linkPago);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarPedidos(String email, Pageable pageable) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId(), pageable)
                .map(pedidoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedido(String email, Long pedidoId) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El pedido no pertenece a este usuario");
        }

        return pedidoMapper.toDTO(pedido);
    }

    public void confirmarPago(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));
        ejecutarConfirmacionPago(pedido);
    }

    private void ejecutarConfirmacionPago(Pedido pedido) {

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            return;
        }

        for (ItemPedido item : pedido.getItems()) {
            VarianteProducto variante = item.getVarianteProducto();
            if (variante != null) {
                variante.setStock(variante.getStock() - item.getCantidad());
                variante.setStockReservado(variante.getStockReservado() - item.getCantidad());
            }
        }

        pedido.setEstado(EstadoPedido.PAGADO);
        pedido.setFechaPago(LocalDateTime.now());

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaActualizacion(LocalDateTime.now());
        pagoRepository.save(pago);
    }

    public void cancelarPedidosExpirados(List<Pedido> expirados) {
        for (Pedido pedido : expirados) {
            for (ItemPedido item : pedido.getItems()) {
                VarianteProducto variante = item.getVarianteProducto();
                if (variante != null) {
                    variante.setStockReservado(variante.getStockReservado() - item.getCantidad());
                }
            }
            pedido.setEstado(EstadoPedido.CANCELADO);
        }
    }

    private Carrito obtenerCarritoValidado(String email) {
        Carrito carrito = gestionarCarritoPort.obtenerCarritoConItems(email);
        if (carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        return carrito;
    }

    private Direccion validarDireccion(Usuario usuario, Long direccionId) {
        return usuario.getDirecciones().stream()
                .filter(d -> d.getId().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada"));
    }

    private void lockearYValidarStock(List<ItemCarrito> items) {
        for (ItemCarrito item : items) {
            VarianteProducto variante = gestionarProductoPort.lockearVariante(item.getVarianteProducto().getId());
            int disponible = variante.getStockDisponible();
            if (disponible < item.getCantidad()) {
                throw new StockInsuficienteException(variante.getSku(), item.getCantidad(), disponible);
            }
        }
    }

    private Pedido crearPedidoConItems(Carrito carrito, Direccion direccion) {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setUsuario(carrito.getUsuario());
        pedido.setDireccionEnvio(direccion);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaExpiracionReserva(LocalDateTime.now().plusMinutes(15));

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCarrito itemCarrito : carrito.getItems()) {
            VarianteProducto variante = itemCarrito.getVarianteProducto();
            int cantidad = itemCarrito.getCantidad();

            variante.setStockReservado(variante.getStockReservado() + cantidad);

            ItemPedido itemPedido = pedidoMapper.carritoItemToItemPedido(itemCarrito);
            itemPedido.setPedido(pedido);
            pedido.getItems().add(itemPedido);

            subtotal = subtotal.add(itemPedido.getSubtotal());
        }

        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(BigDecimal.ZERO);
        pedido.setTotal(subtotal);

        return pedido;
    }

    private String generarNumeroPedido() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime inicioDelDia = LocalDate.now().atStartOfDay();
        long count = pedidoRepository.countByFechaCreacionAfter(inicioDelDia);
        int secuencia = (int) count + 1;
        return String.format("ORD-%s-%03d", fecha, secuencia);
    }
}
