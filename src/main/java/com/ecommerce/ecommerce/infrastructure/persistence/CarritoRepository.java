package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.varianteProducto WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdWithItems(@Param("usuarioId") Long usuarioId);
}
