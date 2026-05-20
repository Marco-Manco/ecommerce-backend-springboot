package com.ecommerce.ecommerce.application.service;

import com.ecommerce.ecommerce.application.dto.AgregarItemCarritoDTO;
import com.ecommerce.ecommerce.application.dto.CarritoDTO;
import com.ecommerce.ecommerce.application.mapper.CarritoMapper;
import com.ecommerce.ecommerce.application.port.out.BuscarUsuarioPort;
import com.ecommerce.ecommerce.application.port.out.GestionarCarritoPort;
import com.ecommerce.ecommerce.application.port.out.GestionarProductoPort;
import com.ecommerce.ecommerce.domain.exception.StockInsuficienteException;
import com.ecommerce.ecommerce.domain.model.Carrito;
import com.ecommerce.ecommerce.domain.model.ItemCarrito;
import com.ecommerce.ecommerce.domain.model.Usuario;
import com.ecommerce.ecommerce.domain.model.VarianteProducto;
import com.ecommerce.ecommerce.infrastructure.persistence.CarritoRepository;
import com.ecommerce.ecommerce.infrastructure.persistence.ItemCarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CarritoService implements GestionarCarritoPort {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final BuscarUsuarioPort buscarUsuarioPort;
    private final GestionarProductoPort gestionarProductoPort;
    private final CarritoMapper carritoMapper;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemCarritoRepository itemCarritoRepository,
                          BuscarUsuarioPort buscarUsuarioPort,
                          GestionarProductoPort gestionarProductoPort,
                          CarritoMapper carritoMapper) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.buscarUsuarioPort = buscarUsuarioPort;
        this.gestionarProductoPort = gestionarProductoPort;
        this.carritoMapper = carritoMapper;
    }

    public CarritoDTO obtenerCarrito(String email) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseGet(() -> crearCarritoVacio(usuario));
        return carritoMapper.toDTO(carrito);
    }

    public CarritoDTO agregarItem(String email, AgregarItemCarritoDTO request) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseGet(() -> crearCarritoVacio(usuario));

        VarianteProducto variante = gestionarProductoPort.buscarVariante(request.varianteProductoId());

        if (!variante.getActivo()) {
            throw new IllegalArgumentException("La variante no está disponible");
        }

        var itemExistente = itemCarritoRepository.findByCarritoIdAndVarianteProductoId(
                carrito.getId(), variante.getId());

        int nuevaCantidad = request.cantidad();
        if (itemExistente.isPresent()) {
            nuevaCantidad += itemExistente.get().getCantidad();
        }

        validarStock(variante, nuevaCantidad);

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(nuevaCantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setVarianteProducto(variante);
            nuevoItem.setCantidad(request.cantidad());
            carrito.getItems().add(nuevoItem);
        }

        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.save(carrito);
        return carritoMapper.toDTO(carrito);
    }

    public CarritoDTO actualizarCantidad(String email, Long itemId, int nuevaCantidad) {
        ItemCarrito item = obtenerYValidarPertenencia(email, itemId);
        validarStock(item.getVarianteProducto(), nuevaCantidad);
        item.setCantidad(nuevaCantidad);
        item.getCarrito().setFechaActualizacion(LocalDateTime.now());
        return carritoMapper.toDTO(item.getCarrito());
    }

    public CarritoDTO eliminarItem(String email, Long itemId) {
        ItemCarrito item = obtenerYValidarPertenencia(email, itemId);
        Carrito carrito = item.getCarrito();
        carrito.getItems().remove(item);
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.save(carrito);
        return carritoMapper.toDTO(carrito);
    }

    public void vaciarCarrito(String email) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        itemCarritoRepository.deleteAllByCarritoId(carrito.getId());
    }

    @Override
    public Carrito obtenerCarritoConItems(String email) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        return carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseGet(() -> crearCarritoVacio(usuario));
    }

    @Override
    public void vaciar(String email) {
        vaciarCarrito(email);
    }

    private ItemCarrito obtenerYValidarPertenencia(String email, Long itemId) {
        Usuario usuario = buscarUsuarioPort.buscarPorEmail(email);
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));
        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El item no pertenece a tu carrito");
        }
        return item;
    }

    private void validarStock(VarianteProducto variante, int cantidad) {
        int disponible = variante.getStockDisponible();
        if (disponible < cantidad) {
            throw new StockInsuficienteException(variante.getSku(), cantidad, disponible);
        }
    }

    private Carrito crearCarritoVacio(Usuario usuario) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        return carritoRepository.save(carrito);
    }
}
