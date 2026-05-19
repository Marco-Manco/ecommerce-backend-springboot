package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.enums.EstadoPedido;
import com.ecommerce.ecommerce.domain.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {
    Page<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.estado = 'PENDIENTE' AND p.fechaExpiracionReserva < :ahora")
    List<Pedido> findReservasExpiradas(@Param("ahora")LocalDateTime ahora);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaCreacion >= :desde")
    long countByFechaCreacionAfter(@Param("desde") LocalDateTime desde);

    List<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);
}
