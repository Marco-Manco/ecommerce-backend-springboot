package com.ecommerce.ecommerce.infrastructure.persistence;

import com.ecommerce.ecommerce.domain.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    Optional<ItemCarrito> findByCarritoIdAndVarianteProductoId(Long carritoId, Long varianteProductoId);

    @Modifying
    @Query("DELETE FROM ItemCarrito ic WHERE ic.carrito.id = :carritoId")
    void deleteAllByCarritoId(@Param("carritoId") Long carritoId);
}
